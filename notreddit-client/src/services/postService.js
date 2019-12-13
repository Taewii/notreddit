import { requestMultipart, get, post, remove } from '../util/APIUtils';
import { API_BASE_URL } from '../util/constants';

const POST_API_URL = API_BASE_URL + '/post';

export function create(createRequest) {
  return requestMultipart({
    url: POST_API_URL + '/create',
    method: 'POST',
    body: createRequest
  });
}

export function edit(createRequest) {
  return requestMultipart({
    url: POST_API_URL + '/edit',
    method: 'PATCH',
    body: createRequest
  });
}

export function findById(id) {
  const url = `${POST_API_URL}/${id}`;
  return get(url);
}

export function findByIdForEditing(id) {
  const url = `${POST_API_URL}/edit/${id}`;
  return get(url);
}

export function allPosts(page, size) {
  const url = `${POST_API_URL}/all?page=${page}&size=${size}`;
  return get(url);
}

export function allSubscribedPosts(page, size, sort) {
  const url = `${POST_API_URL}/subscribed?page=${page}&size=${size}&sort=${sort}`;
  return get(url);
}

export function allPostsFromTheDefaultSubreddits(page, size) {
  const url = `${POST_API_URL}/default-posts?page=${page}&size=${size}`;
  return get(url);
}

export function postsByUsername(page, size, sort, username) {
  const url = `${POST_API_URL}/user/${username}?page=${page}&size=${size}&sort=${sort}`;
  return get(url);
}

export function postsBySubreddit(page, size, sort, subreddit) {
  const url = `${POST_API_URL}/subreddit/${subreddit}?page=${page}&size=${size}&sort=${sort}`;
  return get(url);
}

export function getUpvotedPosts(page, size, sort, username) {
  const url = `${API_BASE_URL}/user/${username}/upvoted?page=${page}&size=${size}&sort=${sort}`;
  return get(url);
}

export function getDownvotedPosts(page, size, sort, username) {
  const url = `${API_BASE_URL}/user/${username}/downvoted?page=${page}&size=${size}&sort=${sort}`;
  return get(url);
}

export function voteForPostAPI(choice, postId) {
  const url = `${POST_API_URL}/vote?choice=${choice}&postId=${postId}`;
  return post(url);
}

export function deletePostById(postId) {
  const url = `${POST_API_URL}/delete?postId=${postId}`;
  return remove(url);
}