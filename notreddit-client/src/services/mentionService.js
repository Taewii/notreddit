import { request } from "../util/APIUtils";
import { API_BASE_URL } from "../util/constants";

const MENTION_API_URL = API_BASE_URL + "/mention";

export function getUnreadMentionsCount() {
  return request({
    url: MENTION_API_URL + '/unread-mentions-count',
    method: 'GET'
  });
}

export function getUsersMentions(page, size) {
  const url = `${MENTION_API_URL}/user-mentions?page=${page}&size=${size}`;

  return request({
    url,
    method: 'GET'
  });
}

export function markAsRead(mentionId) {
  return request({
    url: `${MENTION_API_URL}/read?mentionId=${mentionId}`,
    method: 'POST'
  });
}

export function markAsUnread(mentionId) {
  return request({
    url: `${MENTION_API_URL}/unread?mentionId=${mentionId}`,
    method: 'POST'
  });
}