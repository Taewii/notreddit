import { request } from "../util/APIUtils";
import { API_BASE_URL } from "../util/constants";

export function checkSubredditAvailability(subreddit) {
  return request({
    url: API_BASE_URL + "/subreddit/checkSubredditAvailability?title=" + subreddit,
    method: 'GET'
  });
}

export function isUserSubscribedToSubreddit(subreddit) {
  return request({
    url: API_BASE_URL + "/subreddit/is-subscribed?subreddit=" + subreddit,
    method: 'GET'
  });
}

export function createSubreddit(subredditRequest) {
  return request({
    url: API_BASE_URL + "/subreddit/create",
    method: 'POST',
    body: JSON.stringify(subredditRequest)
  });
}

export function getAllSubreddits() {
  return request({
    url: API_BASE_URL + "/subreddit/all",
    method: 'GET'
  });
}

export function getAllSubredditsWithPostsCount() {
  return request({
    url: API_BASE_URL + "/subreddit/all-with-post-count",
    method: 'GET'
  });
}

export function getUserSubscriptions() {
  return request({
    url: API_BASE_URL + "/subreddit/subscriptions",
    method: 'GET'
  });
}

export function subscribe(subreddit) {
  const url = `${API_BASE_URL}/subreddit/subscribe?subreddit=${subreddit}`;

  return request({
    url,
    method: 'POST'
  });
}

export function unsubscribe(subreddit) {
  const url = `${API_BASE_URL}/subreddit/unsubscribe?subreddit=${subreddit}`;

  return request({
    url,
    method: 'POST'
  });
}