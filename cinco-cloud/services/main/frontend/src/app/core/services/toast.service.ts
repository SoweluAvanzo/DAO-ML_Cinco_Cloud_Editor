import { Injectable } from '@angular/core';

export enum ToastType {
  SUCCESS = 'bg-success',
  INFO = 'bg-info',
  DANGER = 'bg-danger',
  DARK = 'bg-dark'
}

export interface Toast {
  header?: string;
  message: string;
  delay?: number;
  type?: ToastType;
}

@Injectable({
  providedIn: 'root'
})
export class ToastService {

  toasts: Toast[] = [];

  show(toast: Toast): void {
    toast.delay = toast.delay || 5000;
    toast.type = toast.type || ToastType.DARK;
    this.toasts.unshift(toast);
  }

  remove(toast: any): void {
    this.toasts = this.toasts.filter(t => t !== toast);
  }
}
