import { get } from '../util/APIUtils';
import { API_BASE_URL } from '../util/constants';

import { LOGIN_TO_VOTE_MESSAGE } from '../util/messageConstants';
import { errorNotification } from '../util/notifications';

import { voteForCommentAPI } from './commentService';
import { voteForPostAPI } from '../services/postService';
import { GREEN, RED, DEFAULT } from '../util/constants';

const VOTE_API_URL = API_BASE_URL + '/vote';

export function getVoteForPost(postId) {
  return get(VOTE_API_URL + '/post?postId=' + postId);
}

export function getUserVotesForPosts() {
  return get(VOTE_API_URL + '/votes-posts');
}

export function getUserVotesForComments() {
  return get(VOTE_API_URL + '/votes-comments');
}

function handleVoteChange(target, choice) {
  const spans = target.querySelectorAll('span');
  const upvoteLi = spans[0];
  const downvoteLi = spans[2];

  const upvoteSvg = upvoteLi.querySelector('svg');
  const upvoteSpan = upvoteLi.querySelector('span');

  const downvoteSvg = downvoteLi.querySelector('svg');
  const downvoteSpan = downvoteLi.querySelector('span');

  const upvoteStyle = window.getComputedStyle(upvoteSvg);
  const downvoteStyle = window.getComputedStyle(downvoteSvg);

  const upvoteColor = upvoteStyle.getPropertyValue('color');
  const downvoteColor = downvoteStyle.getPropertyValue('color');

  const isUpvoted = upvoteColor === GREEN;
  const isDownvoted = downvoteColor === RED;

  if (isUpvoted) {
    upvoteSpan.textContent = +upvoteSpan.textContent - 1;
  } else if (isDownvoted) {
    downvoteSpan.textContent = +downvoteSpan.textContent - 1;
  }

  if (isUpvoted && choice === 1) {
    upvoteSvg.style.color = DEFAULT;
    return;
  } else if (isDownvoted && choice === -1) {
    downvoteSvg.style.color = DEFAULT;
    return;
  }

  if (choice === 1) {
    upvoteSpan.textContent = +upvoteSpan.textContent + 1;
    upvoteSvg.style.color = GREEN;
    downvoteSvg.style.color = DEFAULT;
  } else if (choice === -1) {
    upvoteSvg.style.color = DEFAULT;
    downvoteSvg.style.color = RED;
    downvoteSpan.textContent = +downvoteSpan.textContent + 1;
  }
}

export function voteForPost(event, choice, postId) {
  const target = event.currentTarget.parentElement.parentElement.parentElement;

  voteForPostAPI(choice, postId)
    .then(res => {
      handleVoteChange(target, choice)
    })
    .catch(error => {
      let message = '';
      if (error.status === 401) {
        message = LOGIN_TO_VOTE_MESSAGE;
      }
      errorNotification(error, message);
    });
};

export function voteForComment(event, choice, commentId) {
  const target = event.currentTarget.parentElement.parentElement.parentElement;

  voteForCommentAPI(choice, commentId)
    .then(res => {
      handleVoteChange(target, choice)
    })
    .catch(error => {
      let message = '';
      if (error.status === 401) {
        message = LOGIN_TO_VOTE_MESSAGE;
      }
      errorNotification(error, message);
    });
};