import { request } from "../util/APIUtils";
import { API_BASE_URL } from "../util/constants";

export function getVoteForPost(postId) {
  return request({
    url: API_BASE_URL + '/user/vote/' + postId,
    method: 'GET'
  });
}