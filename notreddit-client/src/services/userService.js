import { request } from "../util/APIUtils";
import { API_BASE_URL, ACCESS_TOKEN } from "../util/constants";

export function login(loginRequest) {
  return request({
    url: API_BASE_URL + "/auth/signin",
    method: 'POST',
    body: JSON.stringify(loginRequest)
  });
}

export function signup(signupRequest) {
  return request({
    url: API_BASE_URL + "/auth/signup",
    method: 'POST',
    body: JSON.stringify(signupRequest)
  });
}

export function checkUsernameAvailability(username) {
  return request({
    url: API_BASE_URL + "/user/check-username-availability?username=" + username,
    method: 'GET'
  });
}

export function checkEmailAvailability(email) {
  return request({
    url: API_BASE_URL + "/user/check-email-availability?email=" + email,
    method: 'GET'
  });
}

export function getCurrentUser() {
  if (!localStorage.getItem(ACCESS_TOKEN)) {
    return Promise.reject("No access token set.");
  }

  return request({
    url: API_BASE_URL + "/user/me",
    method: 'GET'
  });
}

export function getUserProfile(username) {
  return request({
    url: API_BASE_URL + "/users/" + username,
    method: 'GET'
  });
}

export function getAllUsersWithRoles() {
  if (!localStorage.getItem(ACCESS_TOKEN)) {
    return Promise.reject("No access token set.");
  }

  return request({
    url: API_BASE_URL + "/user/all",
    method: 'GET'
  });
}

export function handleRoleChange(changeRoleRequest) {
  return request({
    url: API_BASE_URL + "/user/change-role",
    method: 'POST',
    body: JSON.stringify(changeRoleRequest)
  });
}

export function getAllRoles() {
  return request({
    url: API_BASE_URL + "/role/all",
    method: 'GET'
  });
}

export function deleteUser(userId) {
  return request({
    url: API_BASE_URL + "/user/delete?id=" + userId,
    method: 'DELETE'
  })
}