import { request } from "../util/APIUtils";
import { API_BASE_URL } from "../util/constants";

export function comment(data) {
  return request({
    url: API_BASE_URL + '/comment/create',
    method: 'POST',
    body: JSON.stringify(data)
  });
}

export function findCommentsForPost(postId) {
  return request({
    url: API_BASE_URL + '/comment/post?postId=' + postId,
    method: 'GET'
  });
}

export function voteForCommentAPI(choice, commentId) {
  const query = `?choice=${choice}&commentId=${commentId}`;

  return request({
    url: API_BASE_URL + '/comment/vote' + query,
    method: 'POST'
  });
}

export function commentsByUsername(username, page, size) {
  const url = `${API_BASE_URL}/comment/user/${username}?page=${page}&size=${size}`;

  return request({
    url,
    method: 'GET'
  });
}

export function deleteCommentById(commentId) {
  const url = `${API_BASE_URL}/comment/delete?commentId=${commentId}`;

  return request({
    url,
    method: 'DELETE'
  });
}

export function editComment(data) {
  return request({
    url: API_BASE_URL + '/comment/edit',
    method: 'PUT',
    body: JSON.stringify(data)
  });
}