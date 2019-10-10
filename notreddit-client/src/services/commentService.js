import { request } from "../util/APIUtils";
import { API_BASE_URL } from "../util/constants";

export function comment(data) {
  return request({
    url: API_BASE_URL + '/comment/post',
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