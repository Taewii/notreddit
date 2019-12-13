import { get, post, remove, patch } from '../util/APIUtils';
import { API_BASE_URL } from '../util/constants';

const COMMENT_API_URL = API_BASE_URL + '/comment';

export function comment(data) {
  return post(API_BASE_URL + '/comment/create', {
    body: JSON.stringify(data)
  });
}

export function editComment(data) {
  return patch(COMMENT_API_URL + '/edit', {
    body: JSON.stringify(data)
  });
}

export function findCommentsForPost(postId, sort) {
  const url = `${COMMENT_API_URL}/post?postId=${postId}&sort=${sort}`;
  return get(url);
}

export function voteForCommentAPI(choice, commentId) {
  const url = `${COMMENT_API_URL}/vote?choice=${choice}&commentId=${commentId}`;
  return post(url);
}

export function commentsByUsername(username, page, size, sort) {
  const url = `${COMMENT_API_URL}/user/${username}?page=${page}&size=${size}&sort=${sort}`;
  return get(url);
}

export function deleteCommentById(commentId) {
  const url = `${COMMENT_API_URL}/delete?commentId=${commentId}`;
  return remove(url);
}