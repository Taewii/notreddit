import { get, post } from '../util/APIUtils';
import { API_BASE_URL } from '../util/constants';

const SUBREDDIT_API_URL = API_BASE_URL + '/subreddit';

export function createSubreddit(subredditRequest) {
  return post(SUBREDDIT_API_URL + '/create', {
    body: JSON.stringify(subredditRequest)
  });
}

export function checkSubredditAvailability(subreddit) {
  return get(SUBREDDIT_API_URL + '/check-subreddit-availability?title=' + subreddit);
}

export function isUserSubscribedToSubreddit(subreddit) {
  return get(SUBREDDIT_API_URL + '/is-subscribed?subreddit=' + subreddit);
}

export function getAllSubreddits() {
  return get(SUBREDDIT_API_URL + '/all');
}

export function getAllSubredditsWithPostsCount() {
  return get(SUBREDDIT_API_URL + '/all-with-post-count');
}

export function getUserSubscriptions() {
  return get(SUBREDDIT_API_URL + '/subscriptions');
}

export function subscribe(subreddit) {
  const url = `${SUBREDDIT_API_URL}/subscribe?subreddit=${subreddit}`;
  return post(url);
}

export function unsubscribe(subreddit) {
  const url = `${SUBREDDIT_API_URL}/unsubscribe?subreddit=${subreddit}`;
  return post(url);
}