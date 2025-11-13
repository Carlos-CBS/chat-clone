import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule],
  template: ``,
  styles: ''
})
export class LoginComponent implements OnInit {

  constructor(
    private authService: AuthService,
  ) {}

  ngOnInit() {
    this.authService.login();
  }
}