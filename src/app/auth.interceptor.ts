import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {

  if (req.url.includes('localhost:8080')) {
    const clonedRequest = req.clone({
      withCredentials: true 
    });
    return next(clonedRequest);
  }
  return next(req);
};