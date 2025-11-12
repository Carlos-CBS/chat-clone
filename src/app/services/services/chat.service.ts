/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { ChatResponse } from '../models/chat-response';
import { create } from '../fn/chat/create';
import { Create$Params } from '../fn/chat/create';
import { getChatsByReceiver } from '../fn/chat/get-chats-by-receiver';
import { GetChatsByReceiver$Params } from '../fn/chat/get-chats-by-receiver';
import { StringResponse } from '../models/string-response';

@Injectable({ providedIn: 'root' })
export class ChatService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `getChatsByReceiver()` */
  static readonly GetChatsByReceiverPath = '/api/v1/chats';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getChatsByReceiver()` instead.
   *
   * This method doesn't expect any request body.
   */
  getChatsByReceiver$Response(params?: GetChatsByReceiver$Params, context?: HttpContext): Observable<StrictHttpResponse<Array<ChatResponse>>> {
    return getChatsByReceiver(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getChatsByReceiver$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getChatsByReceiver(params?: GetChatsByReceiver$Params, context?: HttpContext): Observable<Array<ChatResponse>> {
    return this.getChatsByReceiver$Response(params, context).pipe(
      map((r: StrictHttpResponse<Array<ChatResponse>>): Array<ChatResponse> => r.body)
    );
  }

  /** Path part for operation `create()` */
  static readonly CreatePath = '/api/v1/chats';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `create()` instead.
   *
   * This method doesn't expect any request body.
   */
  create$Response(params: Create$Params, context?: HttpContext): Observable<StrictHttpResponse<StringResponse>> {
    return create(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `create$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  create(params: Create$Params, context?: HttpContext): Observable<StringResponse> {
    return this.create$Response(params, context).pipe(
      map((r: StrictHttpResponse<StringResponse>): StringResponse => r.body)
    );
  }

}
