import { Component, computed, inject } from '@angular/core';
import { Product } from 'app/products/data-access/product.model';
import { ProductsService } from 'app/products/data-access/products.service';
import { WishlistService } from 'app/products/whishlist/data-access/wishlist.service';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';

@Component({
  selector: 'app-wishlist',
  standalone: true,
  imports: [ButtonModule, CardModule],
  templateUrl: './wishlist.component.html',
  styleUrl: './wishlist.component.scss'
})
export class WishlistComponent {
  wishlistService = inject(WishlistService);
  constructor() { this.wishlistService.get().subscribe(); }
}