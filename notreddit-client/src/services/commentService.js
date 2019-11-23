import { request } from "../util/APIUtils";
import { API_BASE_URL } from "../util/constants";

const COMMENT_API_URL = API_BASE_URL + "/comment";

export function comment(data) {
  return request({
    url: API_BASE_URL + '/comment/create',
    method: 'POST',
    body: JSON.stringify(data)
  });
}

export function findCommentsForPost(postId, sort) {
  const url = `${COMMENT_API_URL}/post?postId=${postId}&sort=${sort}`

  return request({
    url,
    method: 'GET'
  });
}

export function voteForCommentAPI(choice, commentId) {
  const query = `?choice=${choice}&commentId=${commentId}`;

  return request({
    url: COMMENT_API_URL + '/vote' + query,
    method: 'POST'
  });
}

export function commentsByUsername(username, page, size, sort) {
  const url = `${COMMENT_API_URL}/user/${username}?page=${page}&size=${size}&sort=${sort}`;

  return request({
    url,
    method: 'GET'
  });
}

export function deleteCommentById(commentId) {
  const url = `${COMMENT_API_URL}/delete?commentId=${commentId}`;

  return request({
    url,
    method: 'DELETE'
  });
}

export function editComment(data) {
  return request({
    url: COMMENT_API_URL + '/edit',
    method: 'PUT',
    body: JSON.stringify(data)
  });
}