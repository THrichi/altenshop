import { Component, inject, computed } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { CartService } from '../../data-access/cart.service';
import { CartItemDTO } from '../../data-access/cart-item.dto';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { FormsModule } from '@angular/forms';
import { PanelMenuModule } from "primeng/panelmenu";

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [
    CommonModule,
    DecimalPipe,
    ButtonModule,
    CardModule,
    ToastModule,
    FormsModule,
    PanelMenuModule
],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.scss'],
})
export class CartComponent {
  cartService = inject(CartService);
  private readonly messageService = inject(MessageService);

  items = this.cartService.items;
  total = computed(() => this.cartService.totalPrice());

  constructor() {
    this.cartService.get().subscribe();
  }

  remove(id: number) {
    this.cartService.remove(id).subscribe();
  }

  clear() {
    this.cartService.clear().subscribe();
  }

  onQtyChange(item: CartItemDTO, event: Event): void {
    const input = event.target as HTMLInputElement;
    const qty = Math.max(1, Math.min(50, Number(input.value) || 1));
    item.quantity = qty;

    this.cartService.updateQuantity(item.productId, qty).subscribe();
  }
}
