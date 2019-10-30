import { request } from "../util/APIUtils";
import { API_BASE_URL } from "../util/constants";

export function getUnreadMentionsCount() {
  return request({
    url: API_BASE_URL + '/mention/unread-mentions-count',
    method: 'GET'
  });
}

export function getUsersMentions(page, size) {
  const url = `${API_BASE_URL}/mention/user-mentions?page=${page}&size=${size}`;

  return request({
    url,
    method: 'GET'
  });
}

export function markAsRead(mentionId) {
  return request({
    url: `${API_BASE_URL}/mention/read?mentionId=${mentionId}`,
    method: 'POST'
  });
}

export function markAsUnread(mentionId) {
  return request({
    url: `${API_BASE_URL}/mention/unread?mentionId=${mentionId}`,
    method: 'POST'
  });
}