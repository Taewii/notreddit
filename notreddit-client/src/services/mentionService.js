import { request } from "../util/APIUtils";
import { API_BASE_URL } from "../util/constants";

export function getUnreadMentionsCount() {
  return request({
    url: API_BASE_URL + '/mention/unread-mentions-count',
    method: 'GET'
  });
}

export function getUsersMentions() {
  return request({
    url: API_BASE_URL + '/mention/user-mentions',
    method: 'GET'
  });
}