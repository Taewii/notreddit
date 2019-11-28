import { get, post, remove } from '../util/APIUtils';
import { API_BASE_URL, ACCESS_TOKEN } from '../util/constants';

const USER_API_URL = API_BASE_URL + '/user';

export function login(loginRequest) {
  return post(API_BASE_URL + '/auth/signin', {
    body: JSON.stringify(loginRequest)
  });
}

export function signup(signupRequest) {
  return post(API_BASE_URL + '/auth/signup', {
    body: JSON.stringify(signupRequest)
  });
}

export function handleRoleChange(changeRoleRequest) {
  return post(USER_API_URL + '/change-role', {
    body: JSON.stringify(changeRoleRequest)
  });
}

export function checkUsernameAvailability(username) {
  return get(USER_API_URL + '/check-username-availability?username=' + username);
}

export function checkEmailAvailability(email) {
  return get(USER_API_URL + '/check-email-availability?email=' + email);
}

export function getCurrentUser() {
  if (!localStorage.getItem(ACCESS_TOKEN)) {
    return Promise.reject('No access token set.');
  }

  return get(USER_API_URL + '/me');
}

export function getAllUsersWithRoles() {
  if (!localStorage.getItem(ACCESS_TOKEN)) {
    return Promise.reject('No access token set.');
  }

  return get(USER_API_URL + '/all');
}

export function getAllRoles() {
  return get(API_BASE_URL + '/role/all');
}

export function deleteUser(userId) {
  return remove(USER_API_URL + '/delete?id=' + userId);
}