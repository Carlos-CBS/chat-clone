import { Component, InputSignal, OnInit, input, output } from '@angular/core';
import { ChatResponse, UserResponse } from '../../services/models';
import { DatePipe } from '@angular/common';
import { ChatService, UserService } from '../../services/services';
import { AuthService, User } from '../../auth';

@Component({
  selector: 'app-chat-list',
  imports: [DatePipe],
  templateUrl: './chat-list.html',
  styleUrl: './chat-list.css',
})
export class ChatList implements OnInit {
  chats: InputSignal<ChatResponse[]> = input<ChatResponse[]>([]);
  searchNewContact = false;
  contacts: Array<UserResponse> = [];
  chatSelected = output<ChatResponse>();
  currentUser: User | null = null;

  constructor(
    private chatService: ChatService,
    private userService: UserService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
      this.authService.user$.subscribe({
        next: (res) => {
          this.currentUser = res;
        }
      })
  }

  searchContact() {
    this.userService.getAllUsers().subscribe({
      next: (users) => {
        this.contacts = users;
        this.searchNewContact = true;
      }
    })
  }

  wrapMessage(lastMessage: string|undefined): String {
    if (lastMessage && lastMessage.length <= 20) {
      return lastMessage
    }
    return lastMessage?.substring(0, 17) + '...';
  }
    chatClicked(chat: ChatResponse) {
      this.chatSelected.emit(chat);
    }

    selectContact(contact: UserResponse) {
      this.chatService.create({
        "senderId": this.currentUser?.sub as string,
        "receiverId": contact.id as string
      }).subscribe({
        next: (res) => {
          const chat: ChatResponse = {
            id: res.response,
            name: contact.name + ' ' + contact.lastName,
            recipientOnline: contact.online,
            lastMessageTime: contact.lastSeen,
            senderId: this.currentUser?.sub,
            receiverId: contact.id
          }
          this.chats().unshift(chat);
          this.searchNewContact = false;
          this.chatSelected.emit(chat);
        }
      });
    }
}
