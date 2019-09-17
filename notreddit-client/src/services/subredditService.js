import { request } from "../util/APIUtils";
import { API_BASE_URL } from "../util/constants";

export function checkSubredditAvailability(subreddit) {
  return request({
    url: API_BASE_URL + "/subreddit/checkSubredditAvailability?title=" + subreddit,
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
  })
}