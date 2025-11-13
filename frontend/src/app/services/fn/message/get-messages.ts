/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { MessageResponse } from '../../models/message-response';

export interface GetMessages$Params {
  chatId: string;
}

export function getMessages(http: HttpClient, rootUrl: string, params: GetMessages$Params, context?: HttpContext): Observable<StrictHttpResponse<Array<MessageResponse>>> {
  const rb = new RequestBuilder(rootUrl, getMessages.PATH, 'get');
  if (params) {
    rb.path('chatId', params.chatId, {});
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<Array<MessageResponse>>;
    })
  );
}

getMessages.PATH = '/api/v1/messages/chat/{chatId}';
