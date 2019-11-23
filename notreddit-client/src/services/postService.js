import { request, requestMultipart } from "../util/APIUtils";
import { API_BASE_URL } from "../util/constants";

const POST_API_URL = API_BASE_URL + "/post";

export function create(createRequest) {
  return requestMultipart({
    url: POST_API_URL + '/create',
    method: 'POST',
    body: createRequest
  });
}

export function findById(id) {
  return request({
    url: POST_API_URL + '/' + id,
    method: 'GET'
  });
}

export function allPosts(page, size) {
  const url = `${POST_API_URL}/all?page=${page}&size=${size}`;

  return request({
    url,
    method: 'GET'
  });
}

export function allSubscribedPosts(page, size, sort) {
  const url = `${POST_API_URL}/subscribed?page=${page}&size=${size}&sort=${sort}`;

  return request({
    url,
    method: 'GET'
  });
}

export function allPostsFromTheDefaultSubreddits(page, size) {
  const url = `${POST_API_URL}/default-posts?page=${page}&size=${size}`;

  return request({
    url,
    method: 'GET'
  });
}

export function postsByUsername(page, size, sort, username) {
  const url = `${POST_API_URL}/user/${username}?page=${page}&size=${size}&sort=${sort}`;

  return request({
    url,
    method: 'GET'
  });
}

export function postsBySubreddit(page, size, sort, subreddit) {
  const url = `${POST_API_URL}/subreddit/${subreddit}?page=${page}&size=${size}&sort=${sort}`;

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
  const url = `${POST_API_URL}/vote?choice=${choice}&postId=${postId}`;

  return request({
    url,
    method: 'POST'
  });
}

export function deletePostById(postId) {
  const url = `${POST_API_URL}/delete?postId=${postId}`;
  return request({
    url,
    method: 'DELETE'
  });
}