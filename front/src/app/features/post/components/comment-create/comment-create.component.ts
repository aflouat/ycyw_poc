import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CommentService } from '../../services/comment.service';
import { PostComment } from '../../interfaces/comment.interface';

@Component({
  selector: 'app-comment-create',
  templateUrl: './comment-create.component.html',
  styleUrls: ['./comment-create.component.scss'],
  standalone: false
})
export class CommentCreateComponent {
  @Input() postId!: number; // Identifiant du post
  commentForm: FormGroup; // Formulaire de création de commentaire
  @Output() commentAdded = new EventEmitter<void>(); // Événement d'ajout de commentaire
 
  constructor(private fb: FormBuilder, private commentService: CommentService) {
    // Initialisation du formulaire
    this.commentForm = this.fb.group({
      content: ['', [Validators.required]], // Champ de contenu obligatoire
    });
  }

  // Soumettre un nouveau commentaire
  onSubmit(): void {
      let newComment: PostComment;
      if (this.commentForm.valid) {
        newComment = {
          postId: this.postId,
          content: this.commentForm.value.content,
          id: 0,
          username: ''
        };

        this.commentService.createComment(newComment).subscribe({
          next: () => {
            // Réinitialiser le formulaire après la création
            this.commentForm.reset();
            this.commentAdded.emit(); // Émettre un événement après ajout
          },
          error: (err) => {
            console.error('Erreur lors de la création du commentaire', err);
          },
        });
      } 
   }
}