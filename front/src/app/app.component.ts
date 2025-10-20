import {
  Component,
  computed,
  inject,
} from "@angular/core";
import { RouterModule } from "@angular/router";
import { SplitterModule } from 'primeng/splitter';
import { ToolbarModule } from 'primeng/toolbar';
import { PanelMenuComponent } from "./shared/ui/panel-menu/panel-menu.component";
import { ToastModule } from "primeng/toast";
import { CartService } from "./products/cart/data-access/cart.service";

@Component({
  selector: "app-root",
  templateUrl: "./app.component.html",
  styleUrls: ["./app.component.scss"],
  standalone: true,
  imports: [RouterModule, SplitterModule, ToolbarModule, PanelMenuComponent,ToastModule],
})
export class AppComponent {
  
  cartService = inject(CartService);

  cartCount = computed(() => this.cartService.getCount());
  title = "ALTEN SHOP";
  
  ngOnInit() {
    this.cartService.get().subscribe();
  }

}
