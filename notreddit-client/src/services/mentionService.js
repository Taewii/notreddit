import { get, post } from '../util/APIUtils';
import { API_BASE_URL } from '../util/constants';

const MENTION_API_URL = API_BASE_URL + '/mention';

export function getUnreadMentionsCount() {
  return get(MENTION_API_URL + '/unread-mentions-count');
}

export function getUsersMentions(page, size) {
  return get(`${MENTION_API_URL}/user-mentions?page=${page}&size=${size}`);
}

export function markAsRead(mentionId) {
  return post(`${MENTION_API_URL}/read?mentionId=${mentionId}`);
}

export function markAsUnread(mentionId) {
  return post(`${MENTION_API_URL}/unread?mentionId=${mentionId}`);
}