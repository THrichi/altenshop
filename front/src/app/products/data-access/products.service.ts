import { Injectable, inject, signal } from "@angular/core";
import { Product } from "./product.model";
import { HttpClient } from "@angular/common/http";
import { catchError, Observable, tap } from "rxjs";
import { environment } from "environments/environment";

@Injectable({
  providedIn: "root"
})
export class ProductsService {

  private readonly http = inject(HttpClient);
  private readonly path = `${environment.apiUrl}/api/products`;
  public totalElements = 0;
  private readonly _products = signal<Product[]>([]);
  public readonly products = this._products.asReadonly();

  public get(page: number = 0, size: number = 12): Observable<any> {
    return this.http.get<any>(`${this.path}?page=${page}&size=${size}`).pipe(
      tap((res) => {
        this._products.set(res.content);
        this.totalElements = res.totalElements; // garde le total pour la pagination
      }),
      catchError((error) => {
        console.error("Error fetching products", error);
        throw error;
      })
    );
  }
  

  public create(product: Product): Observable<Product> {
    return this.http.post<Product>(this.path, product).pipe(
      tap((created) => this._products.update(products => [created, ...products]))
    );
  }

  public update = (product: Product): Observable<Product> => {

    return this.http.put<Product>(`${this.path}/${product.id}`, product).pipe(
      tap((updated) =>
        this._products.update((products) =>
          products.map((p) => (p.id === updated.id ? updated : p))
        )
      )
    );
  };
  

  public delete(productId: number): Observable<void> {
    return this.http.delete<void>(`${this.path}/${productId}`).pipe(
      tap(() => this._products.update(products =>
        products.filter(product => product.id !== productId)
      ))
    );
  }
}
