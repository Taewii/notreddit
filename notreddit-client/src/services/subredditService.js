import { request } from "../util/APIUtils";
import { API_BASE_URL } from "../util/constants";

const SUBREDDIT_API_URL = API_BASE_URL + "/subreddit";

export function checkSubredditAvailability(subreddit) {
  return request({
    url: SUBREDDIT_API_URL + "/check-subreddit-availability?title=" + subreddit,
    method: 'GET'
  });
}

export function isUserSubscribedToSubreddit(subreddit) {
  return request({
    url: SUBREDDIT_API_URL + "/is-subscribed?subreddit=" + subreddit,
    method: 'GET'
  });
}

export function createSubreddit(subredditRequest) {
  return request({
    url: SUBREDDIT_API_URL + "/create",
    method: 'POST',
    body: JSON.stringify(subredditRequest)
  });
}

export function getAllSubreddits() {
  return request({
    url: SUBREDDIT_API_URL + "/all",
    method: 'GET'
  });
}

export function getAllSubredditsWithPostsCount() {
  return request({
    url: SUBREDDIT_API_URL + "/all-with-post-count",
    method: 'GET'
  });
}

export function getUserSubscriptions() {
  return request({
    url: SUBREDDIT_API_URL + "/subscriptions",
    method: 'GET'
  });
}

export function subscribe(subreddit) {
  const url = `${SUBREDDIT_API_URL}/subscribe?subreddit=${subreddit}`;

  return request({
    url,
    method: 'POST'
  });
}

export function unsubscribe(subreddit) {
  const url = `${SUBREDDIT_API_URL}/unsubscribe?subreddit=${subreddit}`;

  return request({
    url,
    method: 'POST'
  });
}