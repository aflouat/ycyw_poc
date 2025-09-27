import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PostListComponent } from './components/post-list/post-list.component';
import { PostRoutingModule } from './post-routing.module';
import { PostDetailComponent } from './components/post-detail/post-detail.component';
import { PostCreateComponent } from './components/post-create/post-create.component';
import { SharedModule } from 'src/app/shared/shared.module';
import { CommentListComponent } from './components/comment-list/comment-list.component';
import { CommentCreateComponent } from "./components/comment-create/comment-create.component";
import { ReactiveFormsModule } from '@angular/forms';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { JwtInterceptor } from 'src/app/interceptors/jwt.interceptor';

@NgModule({
  declarations: [PostListComponent,PostDetailComponent,PostCreateComponent,CommentListComponent,
    CommentCreateComponent],
  imports: [
    PostRoutingModule, CommonModule, SharedModule,ReactiveFormsModule
],
  exports:[]
})
export class PostModule { }
