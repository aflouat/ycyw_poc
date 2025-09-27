import { NgModule } from '@angular/core';
import { MatFormFieldModule, MatLabel } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatMenuModule } from '@angular/material/menu'; // Module pour mat-menu
import {MatIconModule} from '@angular/material/icon';
import {MatCardModule} from '@angular/material/card';
import { DateFormatPipe } from 'src/pipes/date-format.pipe';
import {MatSelectModule} from '@angular/material/select';
import { MatGridListModule } from '@angular/material/grid-list';

  
@NgModule({
  declarations: [
       ],
  imports: [
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    MatToolbarModule,
    MatMenuModule,
   MatIconModule,
   DateFormatPipe,
   MatFormFieldModule,
   MatLabel,
   MatToolbarModule,
MatSelectModule,MatGridListModule
   
  ],
  exports: [
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    MatToolbarModule,
    MatMenuModule,
   MatIconModule,
   DateFormatPipe,
   MatFormFieldModule,
   MatLabel,
   MatToolbarModule,
   MatSelectModule,
   MatGridListModule
  ],
})
export class SharedModule {}
