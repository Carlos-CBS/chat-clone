/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { StringResponse } from '../../models/string-response';

export interface Create$Params {
  senderId: string;
  receiverId: string;
}

export function create(http: HttpClient, rootUrl: string, params: Create$Params, context?: HttpContext): Observable<StrictHttpResponse<StringResponse>> {
  const rb = new RequestBuilder(rootUrl, create.PATH, 'post');
  if (params) {
    rb.query('senderId', params.senderId, {});
    rb.query('receiverId', params.receiverId, {});
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<StringResponse>;
    })
  );
}

create.PATH = '/api/v1/chats';
