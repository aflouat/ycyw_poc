import { Injectable } from "@angular/core";
import { environment } from "../../../../environments/environment";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Observable } from "rxjs";
import { PostComment } from "../interfaces/comment.interface";
import { HttpHeadersService } from "src/app/shared/services/http.headers.service";

@Injectable({
    providedIn: 'root'
  })
  export class CommentService{

    private baseUrl = environment.baseUrl;

    private apiUrl = this.baseUrl+'comment';
  
    constructor(private http: HttpClient, private httpHeadersService:HttpHeadersService) {} 
 
    // Liste des topics (GET)
    getComments(postId:number): Observable<PostComment[]> {
      return this.http.get<PostComment[]>(`${this.apiUrl}/${postId}`, { headers: this.httpHeadersService.getHeaders() });
  
    }

    // Création d'un comment
  createComment(comment:PostComment): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}`, comment,  { headers:  this.httpHeadersService.getHeaders() });
  }
}