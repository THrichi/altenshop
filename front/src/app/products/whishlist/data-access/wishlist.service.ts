import { Injectable, inject, signal, computed } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { environment } from "environments/environment";
import { Observable, tap } from "rxjs";
import { WishlistItemDTO } from "./wishlist-item.dto";

@Injectable({ providedIn: "root" })
export class WishlistService {
  private readonly http = inject(HttpClient);
  private readonly path = `${environment.apiUrl}/api/wishlist`;

  private readonly _items = signal<WishlistItemDTO[]>([]);
  public readonly items = this._items.asReadonly();
  public readonly wishlist = computed<number[]>(() => this._items().map(i => i.productId));

  get(): Observable<WishlistItemDTO[]> {
    return this.http.get<WishlistItemDTO[]>(this.path).pipe(
      tap(items => this._items.set(items))
    );
  }

  add(productId: number): Observable<WishlistItemDTO> {
    return this.http.post<WishlistItemDTO>(`${this.path}/${productId}`, {}).pipe(
      tap(item => {
        if (!this._items().some(i => i.productId === item.productId)) {
          this._items.update(list => [...list, item]);
        }
      })
    );
  }

  remove(productId: number): Observable<void> {
    return this.http.delete<void>(`${this.path}/${productId}`).pipe(
      tap(() => this._items.update(list => list.filter(i => i.productId !== productId)))
    );
  }

  toggle(productId: number): void {
    if (this.isFavorite(productId)) this.remove(productId).subscribe();
    else this.add(productId).subscribe();
  }

  isFavorite(productId: number): boolean {
    return this.wishlist().includes(productId);
  }
}
