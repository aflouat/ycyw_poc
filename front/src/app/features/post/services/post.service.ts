import { Injectable } from "@angular/core";
import { environment } from "../../../../environments/environment";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Observable } from "rxjs";
import { Post } from "../interfaces/post.interface";
import { HttpHeadersService } from "src/app/shared/services/http.headers.service";

@Injectable({
    providedIn: 'root'
  })
  export class PostService{

    private baseUrl = environment.baseUrl;

    private apiUrl = this.baseUrl+'post';
  
    constructor(private http: HttpClient, private httpHeadersService:HttpHeadersService ) {}
  
  
    // Liste des topics (GET)
  public  getPosts(): Observable<Post[]> {
      return this.http.get<Post[]>(this.apiUrl, { headers: this.httpHeadersService.getHeaders() });
  
    }

    detail(id: string): Observable<Post> {
      return this.http.get<Post>(`${this.apiUrl}/${id}`, { headers: this.httpHeadersService.getHeaders() });
      }

            // Création d'un comment
        createPost(post:Post): Observable<void> {
          return this.http.post<void>(`${this.apiUrl}`, post , { headers: this.httpHeadersService.getHeaders() });
        }
      

  }