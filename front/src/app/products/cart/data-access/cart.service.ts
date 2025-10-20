import { Injectable, inject, signal } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { environment } from "environments/environment";
import { Observable, tap } from "rxjs";
import { CartItemDTO } from "./cart-item.dto";

@Injectable({ providedIn: "root" })
export class CartService {
  private readonly http = inject(HttpClient);
  private readonly path = `${environment.apiUrl}/api/cart`;

  private readonly _items = signal<CartItemDTO[]>([]);
  public readonly items = this._items.asReadonly();

  get(): Observable<CartItemDTO[]> {
    return this.http.get<CartItemDTO[]>(this.path).pipe(
      tap(items => this._items.set(items))
    );
  }

  add(productId: number, quantity: number = 1): Observable<CartItemDTO> {
    return this.http.post<CartItemDTO>(`${this.path}/${productId}?quantity=${quantity}`, {}).pipe(
      tap(item => {
        const exists = this._items().some(i => i.productId === item.productId);
        if (exists) {
          this._items.update(list =>
            list.map(i =>
              i.productId === item.productId ? { ...i, ...item } : i
            )
          );
        } else {
          this._items.update(list => [...list, item]);
        }
      })
    );
  }

  updateQuantity(productId: number, quantity: number): Observable<CartItemDTO> {
    return this.http.put<CartItemDTO>(`${this.path}/${productId}?quantity=${quantity}`, {}).pipe(
      tap(updated => {
        this._items.update(list =>
          list.map(i => i.productId === productId ? { ...i, ...updated } : i)
        );
      })
    );
  }
  

  remove(productId: number): Observable<void> {
    return this.http.delete<void>(`${this.path}/${productId}`).pipe(
      tap(() => this._items.update(list => list.filter(i => i.productId !== productId)))
    );
  }

  clear(): Observable<void> {
    return this.http.delete<void>(`${this.path}/clear`).pipe(
      tap(() => this._items.set([]))
    );
  }

  totalPrice(): number {
    return this._items().reduce((sum, item) => sum + item.price * item.quantity, 0);
  }

  getCount(): number {
    return this._items().length;
  }
  
}
