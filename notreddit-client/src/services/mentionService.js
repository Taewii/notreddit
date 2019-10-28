import { request } from "../util/APIUtils";
import { API_BASE_URL } from "../util/constants";

export function getUnreadMentionsCount() {
  return request({
    url: API_BASE_URL + '/user/unread-mentions-count',
    method: 'GET'
  });
}