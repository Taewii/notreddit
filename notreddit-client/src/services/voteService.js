import { request } from "../util/APIUtils";
import { API_BASE_URL } from "../util/constants";

import { vote } from '../services/postService';

import { notification } from 'antd';

export function getVoteForPost(postId) {
  return request({
    url: API_BASE_URL + '/user/vote/' + postId,
    method: 'GET'
  });
}

function handleVoteChange(target, choice) {
  const spans = target.querySelectorAll('span');
  const upvoteLi = spans[0];
  const downvoteLi = spans[2];

  const upvoteSvg = upvoteLi.querySelector('svg');
  const upvoteSpan = upvoteLi.querySelector('span');

  const downvoteSvg = downvoteLi.querySelector('svg');
  const downvoteSpan = downvoteLi.querySelector('span');

  const isUpvoted = !!upvoteSvg.getAttribute('color');
  const isDownvoted = !!downvoteSvg.getAttribute('color');

  if (isUpvoted) {
    upvoteSpan.textContent = +upvoteSpan.textContent - 1;
  } else if (isDownvoted) {
    downvoteSpan.textContent = +downvoteSpan.textContent - 1;
  }

  if (isUpvoted && choice === 1) {
    upvoteSvg.setAttribute('color', '');
    return;
  } else if (isDownvoted && choice === -1) {
    downvoteSvg.setAttribute('color', '');
    return;
  }

  if (choice === 1) {
    upvoteSpan.textContent = +upvoteSpan.textContent + 1;
    upvoteSvg.setAttribute('color', 'green');
    downvoteSvg.setAttribute('color', '');
  } else {
    upvoteSvg.setAttribute('color', '');
    downvoteSvg.setAttribute('color', 'red');
    downvoteSpan.textContent = +downvoteSpan.textContent + 1;
  }
}

export function voteForPost(event, choice, postId) {
  const target = event.currentTarget.parentElement.parentElement.parentElement;

  vote(choice, postId)
    .then(res => {
      handleVoteChange(target, choice)
    })
    .catch(error => {
      let message = error.message || 'Sorry! Something went wrong. Please try again!';
      message = error.status === 401 ? 'You need to be logged in to vote.' : message;
      notification.error({
        message: 'notreddit',
        description: message
      });
    });
};