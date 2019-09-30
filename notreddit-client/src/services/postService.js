import { request, requestMultipart } from "../util/APIUtils";
import { API_BASE_URL } from "../util/constants";

export function create(createRequest) {
  return requestMultipart({
    url: API_BASE_URL + '/post/create',
    method: 'POST',
    body: createRequest
  });
}

export function allPosts() {
  return request({
    url: API_BASE_URL + '/post/all',
    method: 'GET'
  });
}