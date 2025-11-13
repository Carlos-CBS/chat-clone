/* tslint:disable */
/* eslint-disable */
export interface MessageResponse {
  content?: string;
  createdAt?: string;
  id?: number;
  media?: Array<string>;
  receiverId?: string;
  senderId?: string;
  state?: 'SENT' | 'SEEN';
  type?: 'TEXT' | 'IMAGE' | 'AUDIO' | 'VIDEO';
}
