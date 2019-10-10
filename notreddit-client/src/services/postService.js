import { request, requestMultipart } from "../util/APIUtils";
import { API_BASE_URL } from "../util/constants";

export function create(createRequest) {
  return requestMultipart({
    url: API_BASE_URL + '/post/create',
    method: 'POST',
    body: createRequest
  });
}

export function findById(id) {
  return request({
    url: API_BASE_URL + '/post/' + id,
    method: 'GET'
  });
}

export function allPosts() {
  return request({
    url: API_BASE_URL + '/post/all',
    method: 'GET'
  });
}

export function voteForPostAPI(choice, postId) {
  const query = `?choice=${choice}&postId=${postId}`;

  return request({
    url: API_BASE_URL + '/post/vote' + query,
    method: 'POST'
  });
}