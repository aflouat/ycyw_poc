import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { CommentService } from '../../services/comment.service';
import {  PostComment } from '../../interfaces/comment.interface';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-comment-list',
   templateUrl: './comment-list.component.html',
  styleUrls: ['./comment-list.component.scss'],
  standalone:false
})
export class CommentListComponent implements OnInit, OnDestroy{
  postComments:PostComment[]=[];
  errorMessage: string = '';
  @Input() postId!: number; // Post ID reçu du parent
    private postListSubscription: Subscription | undefined;
  
  
  constructor(private commentService:CommentService){}
  ngOnDestroy(): void {
    if(this.postListSubscription){
        this.postListSubscription.unsubscribe();
    }
  }
  ngOnInit(): void {
    console.log("call list comments... for postId:"+this.postId)

    if (this.postId) {
       this.fetchComments(this.postId);
    }
}

    // Récupérer les topics
    fetchComments(postId:number): void {  
      this.postListSubscription = this.commentService.getComments(postId).subscribe({
        next: (data) => (this.postComments = data),
        error: (err) => (this.errorMessage = 'Erreur lors du chargement des posts.'),
      });
   }

   onCommentAdded(): void {
    this.fetchComments(this.postId); // Recharger les commentaires
  }
}
