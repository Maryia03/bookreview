export interface CommentDTO{
  id: number;
  content: string;
  createdDate: string;
  username: string;
  avatarUrl: string;
  likesCount: number;
  dislikesCount: number;
  userReaction: string | null;
}

export interface BookDetails{
  id: number;
  title: string;
  author: string;
  description: string;
  coverUrl: string | null;
  averageRating: number;
  ratingsCount: number;
  userRating: number | null;
  comments: CommentDTO[];
  currentPage: number;
  totalPages: number;
  totalComments: number;
}
