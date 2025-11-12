/* tslint:disable */
/* eslint-disable */
export interface MessageRequest {
  chatId?: string;
  content?: string;
  receiverId?: string;
  senderId?: string;
  type?: 'TEXT' | 'IMAGE' | 'AUDIO' | 'VIDEO';
}
