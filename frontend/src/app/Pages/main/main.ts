import { AfterViewChecked, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ChatList } from "../../Components/chat-list/chat-list";
import type { ChatResponse, MessageRequest, MessageResponse } from '../../services/models';
import { AuthenticationService, ChatService, MessageService } from '../../services/services';
import { User } from '../../auth';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { PickerModule } from '@ctrl/ngx-emoji-mart';
import { FormsModule } from '@angular/forms';
import { EmojiData } from '@ctrl/ngx-emoji-mart/ngx-emoji';
import SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';
import { Notification } from './notification';

@Component({
  standalone: true,
  selector: 'app-main',
  imports: [ChatList, CommonModule, PickerModule, FormsModule, RouterLink],
  templateUrl: './main.html',
  styleUrl: './main.css',
})
export class Main implements OnInit, OnDestroy, AfterViewChecked{


  chats: Array<ChatResponse> = [];
  selectedChat: ChatResponse | null = null;
  chatMessages: MessageResponse[] = [];
  currentUser: User | null = null;
  showEmojis: boolean = false;
  messageContent: string = '';
  socketClient: any = null;
  private notificationSubscription: any;
  @ViewChild('scrollableDiv') scrollableDiv!: ElementRef<HTMLDivElement>;
  
  constructor(
    private chatService: ChatService,
    private router: Router,
    private messageService: MessageService,
    private authenService: AuthenticationService
  ) {}

  ngOnInit(): void {

       this.authenService.getUser().subscribe({
      next: (user) => {
        this.currentUser = user;
        this.getAllChats();
        this.initWebSocket();

      }
    })    
  }

  ngOnDestroy(): void {
      if (this.socketClient != null) {
        this.socketClient.disconnect();
        this.notificationSubscription.unsubscribe();
        this.socketClient = null;
      }
  }
  ngAfterViewChecked(): void {
      this.scrollBottom()
  }

  scrollBottom() {
    if (this.scrollableDiv) {
      const div = this.scrollableDiv.nativeElement;
      div.scrollTop = div.scrollHeight;
    }
  }

  private getAllChats() {
    this.chatService.getChatsByReceiver()
    .subscribe({
      next: (res) => {
        this.chats = res;
      }
    })
  }

    logout() {
      this.authenService.logout().subscribe({
        next: (res) => {
          this.router.navigate(['/login']);
        }
      })
    }

    chatSelected(chatResponse: ChatResponse) {
      console.log("Chat clicked")
      this.selectedChat = chatResponse;
      this.getAllChatMessages(chatResponse.id as string);
      this.setMessagesToSeen();
      this.selectedChat.unreadCount = 0;
    }

    getAllChatMessages(chatId: string) {
      this.messageService.getMessages({'chatId': chatId})
      .subscribe({
        next: (res) => {
          this.chatMessages = res;
        }
      })
    }
      setMessagesToSeen() {
        this.messageService.setMessagesToSeen({
          "chatId": this.selectedChat?.id as string
        }).subscribe({
          next: (res) => {
            console.log("chatid ", res);
          }
        });
      }

      isSelfMessage(message: MessageResponse): boolean {
        return message.senderId === this.currentUser?.sub;
      }

      uploadMedia(target: EventTarget | null) {
        const file = this.extractFileFromTarget(target);
        if (file != null) {
          const reader = new FileReader();
          reader.onload = () => {
            if (reader.result) {
              const mediaLines = reader.result.toString().split(',')[1];

              this.messageService.uploadMedia({
                "chatId": this.selectedChat?.id as string,
                body: {file: file}
              }).subscribe({
                next: () => {
                  const message: MessageResponse = {
                    senderId: this.getSenderId(),
                    receiverId: this.getReceiverId(),
                    content: 'Attachment',
                    type: 'IMAGE',
                    state: 'SENT',
                    media: [mediaLines],
                    createdAt: new Date().toString()
                  };
                  this.chatMessages.push(message);
                }
              });
            }
          }
          reader.readAsDataURL(file);
        }
      }

      extractFileFromTarget(target: EventTarget | null): File | null {
        const htmlInputTarget = target as HTMLInputElement;
        if (target === null || htmlInputTarget.files === null) return null;
        return htmlInputTarget.files[0];
      }

      onSelectEmojis(emojiSelected: any) {
        const emoji: EmojiData = emojiSelected.emoji;
        this.messageContent += emoji.native;
      }

      keyDown(event: KeyboardEvent) {
        if (event.key === "Enter") {
          this.sendMessage();
        }
      }

      sendMessage() {
        if (this.messageContent) {
          const messageRequest: MessageRequest = {
            chatId: this.selectedChat?.id,
            senderId: this.getSenderId(),
            receiverId: this.getReceiverId(),
            content: this.messageContent,
            type: 'TEXT'
          };
          this.messageService.saveMessage({
            body: messageRequest
          }).subscribe({
            next: (res) => {
              const message: MessageResponse = {
                senderId: this.getSenderId(),
                receiverId: this.getReceiverId(),
                content: this.messageContent,
                type: 'TEXT',
                state: 'SENT',
                createdAt: new Date().toString()
              };
              if (!this.selectedChat?.id) return

              this.selectedChat.lastMessage = this.messageContent;
              this.chatMessages.push(message);
              this.messageContent = '';
              this.showEmojis = false;
            }
          })
        }
      }

      onClick() {
        this.setMessagesToSeen();
      }

      private getSenderId() {
        if (this.selectedChat?.senderId === this.currentUser?.sub) {
          return this.selectedChat?.senderId as string;
        }
        return this.selectedChat?.receiverId as string;
      }

      private getReceiverId() {
        if (this.selectedChat?.senderId === this.currentUser?.sub) {
          return this.selectedChat?.receiverId as string;
        }
        return this.selectedChat?.senderId as string;
      }

      private initWebSocket() {
        if (this.currentUser?.sub) {
          let ws = new SockJS('http://localhost:8080/ws');
          this.socketClient = Stomp.over(ws);
          const subUrl = `/users/${this.currentUser.sub}/chat`;
          this.socketClient.connect({'Authorization': 'Bearer ' + this.currentUser.sub},
            () => {
              this.notificationSubscription = this.socketClient.subscribe(subUrl,
                (message: any) => {
                  const notification: Notification = JSON.parse(message.body);

                  this.handleNotification(notification);
                },
                () => console.error("Error while connecting ws")
              )
            }
          )
        }
      }

      private handleNotification(notification: Notification) {
        if (!notification) return;
        if (this.selectedChat && this.selectedChat.id === notification.chatId) {
          switch (notification.type) {
            case 'MESSAGE':
            
            case 'IMAGE':
              const message : MessageResponse = {
                senderId: notification.senderId,
                receiverId: notification.receiverId,
                content: notification.content,
                type: notification.messageType,
                media: notification.media,
                createdAt: new Date().toString()
              };
              if (notification.type === 'IMAGE') {
                this.selectedChat.lastMessage = 'Attachment';
              } else {
                this.selectedChat.lastMessage = notification.content;
              }

              this.chatMessages.push(message);
              break;
            case 'SEEN':
              this.chatMessages.forEach(m => m.state = 'SEEN')
              break;
          }
        } else {
          const destChat = this.chats.find(c => c.id === notification.chatId);
          if (destChat && notification.type !== 'SEEN') {
            if (notification.type === 'MESSAGE') {
              destChat.lastMessage = notification.content;
            } else if (notification.type === 'IMAGE') {
              destChat.lastMessage = 'Attachment';
            }
            destChat.lastMessageTime = new Date().toString();
            destChat.unreadCount! +=1;

          } else {
            if (notification.type === 'MESSAGE') {
              const newChat: ChatResponse = {
                id: notification.chatId,
                senderId: notification.senderId,
                receiverId: notification.receiverId,
                lastMessage: notification.content,
                name: notification.chatName,
                unreadCount: 1,
                lastMessageTime: new Date().toString()
              }
              this.chats.unshift(newChat);
            }
          }
        }
      }
}
