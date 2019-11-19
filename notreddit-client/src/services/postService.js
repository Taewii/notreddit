import { request, requestMultipart } from "../util/APIUtils";
import { API_BASE_URL } from "../util/constants";

export function create(createRequest) {
  return requestMultipart({
    url: API_BASE_URL + '/post/create',
    method: 'POST',
    body: createRequest
  });
}

export function findById(id) {
  return request({
    url: API_BASE_URL + '/post/' + id,
    method: 'GET'
  });
}

export function allPosts(page, size) {
  const url = `${API_BASE_URL}/post/all?page=${page}&size=${size}`;

  return request({
    url,
    method: 'GET'
  });
}

export function allSubscribedPosts(page, size, sort) {
  const url = `${API_BASE_URL}/post/subscribed?page=${page}&size=${size}&sort=${sort}`;

  return request({
    url,
    method: 'GET'
  });
}

export function allPostsFromTheDefaultSubreddits(page, size) {
  const url = `${API_BASE_URL}/post/default-posts?page=${page}&size=${size}`;

  return request({
    url,
    method: 'GET'
  });
}

export function postsByUsername(page, size, sort, username) {
  const url = `${API_BASE_URL}/post/user/${username}?page=${page}&size=${size}&sort=${sort}`;

  return request({
    url,
    method: 'GET'
  });
}

export function postsBySubreddit(page, size, sort, subreddit) {
  const url = `${API_BASE_URL}/post/subreddit/${subreddit}?page=${page}&size=${size}&sort=${sort}`;

  return request({
    url,
    method: 'GET'
  });
}

export function getUpvotedPosts(page, size, sort, username) {
  const url = `${API_BASE_URL}/user/${username}/upvoted?page=${page}&size=${size}&sort=${sort}`;

  return request({
    url,
    method: 'GET'
  });
}

export function getDownvotedPosts(page, size, sort, username) {
  const url = `${API_BASE_URL}/user/${username}/downvoted?page=${page}&size=${size}&sort=${sort}`;

  return request({
    url,
    method: 'GET'
  });
}

export function voteForPostAPI(choice, postId) {
  const url = `${API_BASE_URL}/post/vote?choice=${choice}&postId=${postId}`;

  return request({
    url,
    method: 'POST'
  });
}

export function deletePostById(postId) {
  const url = `${API_BASE_URL}/post/delete?postId=${postId}`;
  return request({
    url,
    method: 'DELETE'
  });
}