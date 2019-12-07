SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_with_oids = false;

CREATE TABLE public.comments
(
    id         uuid                        NOT NULL,
    content    text                        NOT NULL,
    created_on timestamp without time zone NOT NULL,
    downvotes  numeric DEFAULT 0           NOT NULL,
    upvotes    numeric DEFAULT 0           NOT NULL,
    creator_id uuid                        NOT NULL,
    parent_id  uuid,
    post_id    uuid                        NOT NULL
);

CREATE TABLE public.files
(
    thumbnail_url character varying(255),
    url           character varying(255) NOT NULL,
    post_id       uuid                   NOT NULL
);

CREATE TABLE public.mentions
(
    id          uuid                        NOT NULL,
    created_on  timestamp without time zone NOT NULL,
    is_read     boolean DEFAULT false       NOT NULL,
    comment_id  uuid                        NOT NULL,
    creator_id  uuid                        NOT NULL,
    receiver_id uuid                        NOT NULL
);

CREATE TABLE public.posts
(
    id           uuid                        NOT NULL,
    content      text,
    created_on   timestamp without time zone NOT NULL,
    downvotes    numeric DEFAULT 0           NOT NULL,
    title        character varying(255)      NOT NULL,
    upvotes      numeric DEFAULT 0           NOT NULL,
    creator_id   uuid                        NOT NULL,
    subreddit_id bigint                      NOT NULL
);

CREATE TABLE public.roles
(
    id        bigint                 NOT NULL,
    authority character varying(255) NOT NULL
);

CREATE SEQUENCE public.roles_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.roles_id_seq OWNED BY public.roles.id;

CREATE TABLE public.subreddits
(
    id         bigint                 NOT NULL,
    title      character varying(255) NOT NULL,
    creator_id uuid                   NOT NULL
);

CREATE SEQUENCE public.subreddits_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.subreddits_id_seq OWNED BY public.subreddits.id;

CREATE TABLE public.user_subscriptions
(
    user_id      uuid   NOT NULL,
    subreddit_id bigint NOT NULL
);

CREATE TABLE public.users
(
    id       uuid                   NOT NULL,
    email    character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    username character varying(255) NOT NULL
);

CREATE TABLE public.users_roles
(
    user_id uuid   NOT NULL,
    role_id bigint NOT NULL
);

CREATE TABLE public.votes
(
    id         uuid                        NOT NULL,
    choice     smallint                    NOT NULL,
    created_on timestamp without time zone NOT NULL,
    comment_id uuid,
    post_id    uuid,
    user_id    uuid                        NOT NULL
);

ALTER TABLE ONLY public.roles
    ALTER COLUMN id SET DEFAULT nextval('public.roles_id_seq'::regclass);

ALTER TABLE ONLY public.subreddits
    ALTER COLUMN id SET DEFAULT nextval('public.subreddits_id_seq'::regclass);

COPY public.comments (id, content, created_on, downvotes, upvotes, creator_id, parent_id, post_id) FROM stdin;
6ba627e8-7a3f-4e9b-ae44-675ccdbc4cf2	sup	2019-11-29 20:09:52.956014	1	0	0cd5ebf9-1023-4164-81ad-e09e92f9cff2	fb7315c8-65ec-417f-8b75-eabf98fd3eca	21c8152f-61cc-4b88-a48d-0771e1396abb
a8882137-abad-41c3-a112-c9aedebb2ba5	[deleted]	2019-11-08 19:20:08.597613	0	0	0cd5ebf9-1023-4164-81ad-e09e92f9cff2	ff3b4efc-34b0-4dc9-91ab-8c99ac5140cc	6d72346f-f359-40d7-b988-4f5cbc73ff5a
95a28861-17f1-4823-bd73-f1a0815b6284	hwatawaw	2019-11-08 20:44:11.802461	0	0	0cd5ebf9-1023-4164-81ad-e09e92f9cff2	\N	d92e1999-fd40-4ed8-b72a-faa16b54da4f
9c4c99fa-f989-44e7-ba4d-c7144547c838	bruhhaha	2019-11-08 20:54:29.735424	0	0	0cd5ebf9-1023-4164-81ad-e09e92f9cff2	953fa822-df52-4402-85a6-d6380b930522	d92e1999-fd40-4ed8-b72a-faa16b54da4f
fb7315c8-65ec-417f-8b75-eabf98fd3eca	hey	2019-11-29 20:09:33.871662	1	0	24cc00bb-7b43-4e69-ac5c-f0b85d5fdc71	\N	21c8152f-61cc-4b88-a48d-0771e1396abb
fb8727dd-e7b7-4611-84c8-fc39e7aa6440	no	2019-10-24 21:42:37.31409	0	1	0cd5ebf9-1023-4164-81ad-e09e92f9cff2	\N	d92e1999-fd40-4ed8-b72a-faa16b54da4f
f245644d-ed7e-4661-887e-97273c48924b	op	2019-11-29 20:10:45.677018	1	0	0cd5ebf9-1023-4164-81ad-e09e92f9cff2	fb7315c8-65ec-417f-8b75-eabf98fd3eca	21c8152f-61cc-4b88-a48d-0771e1396abb
ff3b4efc-34b0-4dc9-91ab-8c99ac5140cc	[deleted]	2019-11-08 19:20:03.903551	0	0	0cd5ebf9-1023-4164-81ad-e09e92f9cff2	\N	6d72346f-f359-40d7-b988-4f5cbc73ff5a
953fa822-df52-4402-85a6-d6380b930522	damn gurl	2019-10-24 22:39:50.137343	0	1	46cf2027-3503-4168-9d58-c5f4b81db30a	fb8727dd-e7b7-4611-84c8-fc39e7aa6440	d92e1999-fd40-4ed8-b72a-faa16b54da4f
478d3cc6-6cdf-4885-8ae8-da20b107b21a	eyyy	2019-11-15 20:19:43.805768	0	0	24cc00bb-7b43-4e69-ac5c-f0b85d5fdc71	d5ac3df1-9c6e-40fc-b26e-99fbc909ef70	d92e1999-fd40-4ed8-b72a-faa16b54da4f
86a3f78c-9beb-4382-9cb6-6bbdd92ebcb0	opa	2019-10-24 22:48:02.713374	1	0	46cf2027-3503-4168-9d58-c5f4b81db30a	\N	d92e1999-fd40-4ed8-b72a-faa16b54da4f
9872c78e-0d11-4227-a0ca-c1178899094e	sup	2019-10-29 19:50:24.495058	1	0	0cd5ebf9-1023-4164-81ad-e09e92f9cff2	\N	afa404b9-c5d0-48de-85a5-77ffafadb941
a63864c1-cd35-4282-9eb8-e22212ffffe9	op	2019-11-19 22:12:11.044622	0	0	0cd5ebf9-1023-4164-81ad-e09e92f9cff2	\N	d007bf28-190b-4123-ae10-69b8e8bd226f
d5ac3df1-9c6e-40fc-b26e-99fbc909ef70	hwatt	2019-10-27 19:15:32.671614	0	1	0cd5ebf9-1023-4164-81ad-e09e92f9cff2	953fa822-df52-4402-85a6-d6380b930522	d92e1999-fd40-4ed8-b72a-faa16b54da4f
14c6c3af-5f09-4f31-a5e1-88f2bb26be81	hello	2019-11-23 23:09:40.433695	1	0	0cd5ebf9-1023-4164-81ad-e09e92f9cff2	\N	27543823-8272-4c68-8295-e37a63018a6b
\.

COPY public.files (thumbnail_url, url, post_id) FROM stdin;
https://api.screenshotmachine.com/?key=6d07bd&url=https%3A%2F%2Fi.redd.it%2Fm2cry0bo74p31.jpg	https://i.redd.it/m2cry0bo74p31.jpg	6d72346f-f359-40d7-b988-4f5cbc73ff5a
https://api.screenshotmachine.com/?key=6d07bd&url=https%3A%2F%2Fstreamable.com%2Fyihqy	https://streamable.com/yihqy	21c8152f-61cc-4b88-a48d-0771e1396abb
https://api.screenshotmachine.com/?key=6d07bd&url=https%3A%2F%2Fwww.reddit.com%2Fhot%2F	https://www.reddit.com/hot/	afa404b9-c5d0-48de-85a5-77ffafadb941
https://api.screenshotmachine.com/?key=6d07bd&url=https%3A%2F%2Fwww.dropbox.com%2Fs%2Fkfc1ibccy7mgpj2%2F070_1000.jpg%3Fdl%3D0%26raw%3D1	https://www.dropbox.com/s/kfc1ibccy7mgpj2/070_1000.jpg?dl=0&raw=1	27543823-8272-4c68-8295-e37a63018a6b
https://www.dropbox.com/s/me4equwsj95buph/1003099_400225100093992_726287326_n.jpg?dl=0&raw=1	https://www.dropbox.com/s/me4equwsj95buph/1003099_400225100093992_726287326_n.jpg?dl=0&raw=1	f6ccac45-2b61-41be-aa14-eab19d2a1379
https://api.screenshotmachine.com/?key=6d07bd&url=https%3A%2F%2Fwww.dropbox.com%2Fs%2Fznxt2fpmn0aow84%2F10542479_730645697027720_1911132328_n.mp4%3Fdl%3D0%26raw%3D1	https://www.dropbox.com/s/znxt2fpmn0aow84/10542479_730645697027720_1911132328_n.mp4?dl=0&raw=1	965c0aa7-0fec-421c-a11e-57bd4266465f
https://www.dropbox.com/s/xfjwduhyzs5wtyy/1010783_815818905111668_384454518_n.jpg?dl=0&raw=1	https://www.dropbox.com/s/xfjwduhyzs5wtyy/1010783_815818905111668_384454518_n.jpg?dl=0&raw=1	d92e1999-fd40-4ed8-b72a-faa16b54da4f
https://api.screenshotmachine.com/?key=6d07bd&url=https%3A%2F%2Fstreamable.com%2Ftkok7	https://streamable.com/tkok7	c1713ccd-97e8-4b50-9d6e-aa0fa2022787
https://www.dropbox.com/s/e6dbbu327xr95nc/040_1000.jpg?dl=0&raw=1	https://www.dropbox.com/s/e6dbbu327xr95nc/040_1000.jpg?dl=0&raw=1	c2089bdf-9ec9-41d2-b21a-2ef4db86fee4
https://api.screenshotmachine.com/?key=6d07bd&url=https%3A%2F%2Fi.imgur.com%2F28sycSO.jpg	https://i.imgur.com/28sycSO.jpg	730e6c67-2fbf-49d0-9762-42364a7841c2
\.

COPY public.mentions (id, created_on, is_read, comment_id, creator_id, receiver_id) FROM stdin;
0a90ca93-174a-49a7-ac2f-f07d08058307	2019-10-24 22:40:08.328691	f	953fa822-df52-4402-85a6-d6380b930522	46cf2027-3503-4168-9d58-c5f4b81db30a	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
04256266-d201-43d6-8df2-534c2d36fd07	2019-10-24 22:48:17.006375	f	86a3f78c-9beb-4382-9cb6-6bbdd92ebcb0	46cf2027-3503-4168-9d58-c5f4b81db30a	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
b79969b2-8212-44ec-87aa-b2d03cc8591c	2019-10-27 19:15:32.679619	f	d5ac3df1-9c6e-40fc-b26e-99fbc909ef70	0cd5ebf9-1023-4164-81ad-e09e92f9cff2	46cf2027-3503-4168-9d58-c5f4b81db30a
03716bba-532e-4b91-b257-3eb87653219a	2019-11-23 23:09:40.433695	t	14c6c3af-5f09-4f31-a5e1-88f2bb26be81	0cd5ebf9-1023-4164-81ad-e09e92f9cff2	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
c1ab8fd0-db92-49c2-90c4-dac47a1f467e	2019-11-19 22:12:11.044622	t	a63864c1-cd35-4282-9eb8-e22212ffffe9	0cd5ebf9-1023-4164-81ad-e09e92f9cff2	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
2fe9bdd1-00b6-4fe5-a98e-2cbd4736eabe	2019-11-29 20:09:52.962019	f	6ba627e8-7a3f-4e9b-ae44-675ccdbc4cf2	0cd5ebf9-1023-4164-81ad-e09e92f9cff2	24cc00bb-7b43-4e69-ac5c-f0b85d5fdc71
a0eabfbc-666f-4b6a-8806-b8383957e61e	2019-11-29 20:10:45.683019	t	f245644d-ed7e-4661-887e-97273c48924b	0cd5ebf9-1023-4164-81ad-e09e92f9cff2	24cc00bb-7b43-4e69-ac5c-f0b85d5fdc71
25a729d6-7b04-4951-be3b-ee477c7f804a	2019-11-29 20:09:33.871662	t	fb7315c8-65ec-417f-8b75-eabf98fd3eca	24cc00bb-7b43-4e69-ac5c-f0b85d5fdc71	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
752c2e41-5bd6-4bcf-ba76-7a1c5c4e137e	2019-11-08 20:44:11.802461	t	95a28861-17f1-4823-bd73-f1a0815b6284	0cd5ebf9-1023-4164-81ad-e09e92f9cff2	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
a4816f9e-063d-41c7-8a95-415bae45768b	2019-11-08 20:54:29.739429	t	9c4c99fa-f989-44e7-ba4d-c7144547c838	0cd5ebf9-1023-4164-81ad-e09e92f9cff2	46cf2027-3503-4168-9d58-c5f4b81db30a
c9527ee0-5538-4d8f-a08f-7842648550dc	2019-10-29 19:50:24.495058	f	9872c78e-0d11-4227-a0ca-c1178899094e	0cd5ebf9-1023-4164-81ad-e09e92f9cff2	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
ba094cd5-1a20-4554-ad36-15f140a96e1a	2019-11-15 20:19:43.810769	f	478d3cc6-6cdf-4885-8ae8-da20b107b21a	24cc00bb-7b43-4e69-ac5c-f0b85d5fdc71	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
\.

COPY public.posts (id, content, created_on, downvotes, title, upvotes, creator_id, subreddit_id) FROM stdin;
730e6c67-2fbf-49d0-9762-42364a7841c2	te	2019-11-28 22:16:47.960722	0	test with url	0	0cd5ebf9-1023-4164-81ad-e09e92f9cff2	8
27543823-8272-4c68-8295-e37a63018a6b	test	2019-09-27 19:46:41.828787	0	dropboxtest	1	0cd5ebf9-1023-4164-81ad-e09e92f9cff2	8
d92e1999-fd40-4ed8-b72a-faa16b54da4f	do i really need content?	2019-10-17 20:06:13.699841	1	some hoe	2	0cd5ebf9-1023-4164-81ad-e09e92f9cff2	7
c1713ccd-97e8-4b50-9d6e-aa0fa2022787		2019-11-11 12:22:11.229646	0	Khabib Nurmagomedov helps Pat Healy across the street	0	0cd5ebf9-1023-4164-81ad-e09e92f9cff2	12
9ffd90cf-b02b-4c5a-a90b-1715d1fe417c	nah	2019-10-20 20:23:03.722246	0	admin testeroni	4	46cf2027-3503-4168-9d58-c5f4b81db30a	7
6d72346f-f359-40d7-b988-4f5cbc73ff5a	some stuff	2019-09-27 15:56:05.633482	0	Ribbons	1	0cd5ebf9-1023-4164-81ad-e09e92f9cff2	7
c2089bdf-9ec9-41d2-b21a-2ef4db86fee4		2019-11-28 20:12:18.511597	0	Damn bitch	2	0cd5ebf9-1023-4164-81ad-e09e92f9cff2	338
d007bf28-190b-4123-ae10-69b8e8bd226f		2019-10-17 20:07:49.760249	0	texterino	3	0cd5ebf9-1023-4164-81ad-e09e92f9cff2	7
21c8152f-61cc-4b88-a48d-0771e1396abb	vid	2019-09-27 16:07:43.404367	1	lets see how it works with videos	5	0cd5ebf9-1023-4164-81ad-e09e92f9cff2	9
afa404b9-c5d0-48de-85a5-77ffafadb941		2019-09-27 16:10:35.059637	0	web page	2	0cd5ebf9-1023-4164-81ad-e09e92f9cff2	7
965c0aa7-0fec-421c-a11e-57bd4266465f	cnt	2019-09-27 20:57:39.221701	2	videotest	2	0cd5ebf9-1023-4164-81ad-e09e92f9cff2	7
b6f9ee49-88b7-438b-aa90-a978fe8ff973	text	2019-09-27 16:11:40.073301	0	just text	1	0cd5ebf9-1023-4164-81ad-e09e92f9cff2	7
f6ccac45-2b61-41be-aa14-eab19d2a1379	pic	2019-09-27 20:22:18.928161	0	some new picture	1	0cd5ebf9-1023-4164-81ad-e09e92f9cff2	7
821c2de8-d9ec-4e07-9c00-c8095eb27ed4	pic	2019-09-28 20:22:18.928161	0	some new picture	0	24cc00bb-7b43-4e69-ac5c-f0b85d5fdc71	482
ca95bfad-f810-4cda-965c-154a86761cf9	pic	2019-09-28 20:22:18.928161	0	some new picture	0	24cc00bb-7b43-4e69-ac5c-f0b85d5fdc71	482
\.

COPY public.roles (id, authority) FROM stdin;
1	ROOT
2	ADMIN
3	MODERATOR
4	USER
\.

COPY public.subreddits (id, title, creator_id) FROM stdin;
6	eli5	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
8	HumansBeingBros	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
9	EyeBleach	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
10	hiphopheads	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
11	kanye	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
12	bjj	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
7	aww	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
13	boxing	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
14	sports	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
15	mma	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
106	heyy	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
338	bulgaria	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
342	testeroni	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
367	news	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
482	random	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
\.

COPY public.user_subscriptions (user_id, subreddit_id) FROM stdin;
46cf2027-3503-4168-9d58-c5f4b81db30a	13
46cf2027-3503-4168-9d58-c5f4b81db30a	9
46cf2027-3503-4168-9d58-c5f4b81db30a	8
0cd5ebf9-1023-4164-81ad-e09e92f9cff2	8
0cd5ebf9-1023-4164-81ad-e09e92f9cff2	12
24cc00bb-7b43-4e69-ac5c-f0b85d5fdc71	7
24cc00bb-7b43-4e69-ac5c-f0b85d5fdc71	9
24cc00bb-7b43-4e69-ac5c-f0b85d5fdc71	8
0cd5ebf9-1023-4164-81ad-e09e92f9cff2	7
0cd5ebf9-1023-4164-81ad-e09e92f9cff2	9
0cd5ebf9-1023-4164-81ad-e09e92f9cff2	342
\.

COPY public.users (id, email, password, username) FROM stdin;
0cd5ebf9-1023-4164-81ad-e09e92f9cff2	email@abv.bg	$2y$10$rg502wzMJV3RpQXl/lua1.XutQE2GnxQUUNWcn45xelCxgScVGERu	root
46cf2027-3503-4168-9d58-c5f4b81db30a	email1@abv.bg	$2y$10$nLNM7DsL0yxMnlSVuO8bje4Si29aEK45UPWQhl5tEkWwamh1cjeb6	admin
6f912194-dda1-4ad1-ac4c-846071dc11ad	email2@abv.bg	$2y$10$M1koOpzgFp/pmdjCOdW8UOgPq.FTza7bNm6T2HN8TR7afoptcux2q	moderator
24cc00bb-7b43-4e69-ac5c-f0b85d5fdc71	email3@abv.bg	$2y$10$mbnM2PGX87NFc8DEskOy5OBSz7YpWAL8Rx5mIvQ51iqUqLgGofYiy	user
\.

COPY public.users_roles (user_id, role_id) FROM stdin;
0cd5ebf9-1023-4164-81ad-e09e92f9cff2	1
0cd5ebf9-1023-4164-81ad-e09e92f9cff2	2
0cd5ebf9-1023-4164-81ad-e09e92f9cff2	3
0cd5ebf9-1023-4164-81ad-e09e92f9cff2	4
46cf2027-3503-4168-9d58-c5f4b81db30a	4
6f912194-dda1-4ad1-ac4c-846071dc11ad	4
6f912194-dda1-4ad1-ac4c-846071dc11ad	3
46cf2027-3503-4168-9d58-c5f4b81db30a	2
46cf2027-3503-4168-9d58-c5f4b81db30a	3
24cc00bb-7b43-4e69-ac5c-f0b85d5fdc71	4
\.

COPY public.votes (id, choice, created_on, comment_id, post_id, user_id) FROM stdin;
d54e0d59-222f-40f4-8c2f-f132f1556c2b	1	2019-11-10 19:55:03.782561	953fa822-df52-4402-85a6-d6380b930522	\N	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
65396c06-e46b-4525-854e-5edea2a12b94	1	2019-11-15 20:15:08.023514	\N	d007bf28-190b-4123-ae10-69b8e8bd226f	24cc00bb-7b43-4e69-ac5c-f0b85d5fdc71
0c001ef1-6428-45c7-bcb3-169c8df8ce4e	-1	2019-11-15 20:25:03.418719	\N	d92e1999-fd40-4ed8-b72a-faa16b54da4f	46cf2027-3503-4168-9d58-c5f4b81db30a
9e56d906-79d6-4aa6-ab93-a8b9a8d71942	-1	2019-11-18 18:04:32.837025	86a3f78c-9beb-4382-9cb6-6bbdd92ebcb0	\N	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
8ecf49e7-0bab-47bc-9de7-0f79131465be	1	2019-11-19 17:06:09.666942	\N	9ffd90cf-b02b-4c5a-a90b-1715d1fe417c	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
205f499f-2a51-480c-9d4d-e4cb2f4692da	-1	2019-11-19 20:18:04.751856	9872c78e-0d11-4227-a0ca-c1178899094e	\N	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
7f62c27d-d214-4d1e-b423-a9c9c06abd95	1	2019-11-19 22:08:05.116886	\N	d92e1999-fd40-4ed8-b72a-faa16b54da4f	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
a3602857-b1c2-488a-acd3-3bfea0772051	-1	2019-11-26 17:53:37.161959	\N	965c0aa7-0fec-421c-a11e-57bd4266465f	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
8258312e-2886-4779-823e-8dee657147a6	1	2019-11-28 20:12:45.421761	\N	c2089bdf-9ec9-41d2-b21a-2ef4db86fee4	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
c4be65f0-9eb9-46a1-94b2-b8069eea5f1e	-1	2019-11-28 22:56:21.908879	14c6c3af-5f09-4f31-a5e1-88f2bb26be81	\N	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
efc50f91-847f-442d-aad4-0ffd42551159	-1	2019-11-29 20:45:55.754969	\N	21c8152f-61cc-4b88-a48d-0771e1396abb	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
29f7ed3d-6c3a-4b96-8d2a-1d2e09f5a5d6	1	2019-10-31 14:52:43.555758	\N	f6ccac45-2b61-41be-aa14-eab19d2a1379	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
b2ce3fe3-8a3c-474b-b4b0-869fac195d89	1	2019-11-05 23:19:56.407397	d5ac3df1-9c6e-40fc-b26e-99fbc909ef70	\N	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
2fe63b60-0418-4fe0-a5c4-1169e2b02cec	1	2019-10-31 14:52:59.816235	fb8727dd-e7b7-4611-84c8-fc39e7aa6440	\N	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
b647e0aa-637e-42f2-985e-3d2c0dbcfd9a	-1	2019-11-29 20:46:02.360973	6ba627e8-7a3f-4e9b-ae44-675ccdbc4cf2	\N	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
241ff824-4b8c-4cdf-8fa4-0ff0803d964e	-1	2019-11-29 20:55:00.991632	fb7315c8-65ec-417f-8b75-eabf98fd3eca	\N	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
f5b54cc8-1c24-481c-8054-174762972595	1	2019-11-29 20:59:07.776147	\N	965c0aa7-0fec-421c-a11e-57bd4266465f	24cc00bb-7b43-4e69-ac5c-f0b85d5fdc71
10f6dedd-ad6e-4aef-9d8e-542b588a9f14	1	2019-11-30 22:49:04.082545	\N	27543823-8272-4c68-8295-e37a63018a6b	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
f5f92ed3-d3fe-4da5-8435-98f2d635c97a	-1	2019-11-30 22:52:10.106215	f245644d-ed7e-4661-887e-97273c48924b	\N	0cd5ebf9-1023-4164-81ad-e09e92f9cff2
\.

SELECT pg_catalog.setval('public.roles_id_seq', 3208, true);

SELECT pg_catalog.setval('public.subreddits_id_seq', 578, true);

ALTER TABLE ONLY public.comments
    ADD CONSTRAINT comments_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.files
    ADD CONSTRAINT files_pkey PRIMARY KEY (post_id);

ALTER TABLE ONLY public.mentions
    ADD CONSTRAINT mentions_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.posts
    ADD CONSTRAINT posts_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.subreddits
    ADD CONSTRAINT subreddits_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT uk_nb4h0p6txrmfc0xbrd1kglp9t UNIQUE (authority);

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_r43af9ap4edm43mmtq01oddj6 UNIQUE (username);

ALTER TABLE ONLY public.subreddits
    ADD CONSTRAINT uq_title UNIQUE (title);

ALTER TABLE ONLY public.user_subscriptions
    ADD CONSTRAINT user_subscriptions_pkey PRIMARY KEY (user_id, subreddit_id);

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.users_roles
    ADD CONSTRAINT users_roles_pkey PRIMARY KEY (user_id, role_id);

ALTER TABLE ONLY public.votes
    ADD CONSTRAINT votes_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.votes
    ADD CONSTRAINT fk1m2jqtro85c13ya5kv0kvkc97 FOREIGN KEY (post_id) REFERENCES public.posts (id);

ALTER TABLE ONLY public.subreddits
    ADD CONSTRAINT fk2ayx2d6rphbce10m6di3hw2tm FOREIGN KEY (creator_id) REFERENCES public.users (id);

ALTER TABLE ONLY public.users_roles
    ADD CONSTRAINT fk2o0jvgh89lemvvo17cbqvdxaa FOREIGN KEY (user_id) REFERENCES public.users (id);

ALTER TABLE ONLY public.user_subscriptions
    ADD CONSTRAINT fk3l40lbyji8kj5xoc20ycwsc8g FOREIGN KEY (user_id) REFERENCES public.users (id);

ALTER TABLE ONLY public.mentions
    ADD CONSTRAINT fk5u9f9whcbf3x7h5vmoevbfch2 FOREIGN KEY (creator_id) REFERENCES public.users (id);

ALTER TABLE ONLY public.mentions
    ADD CONSTRAINT fk732qujc59y3hgle6p1cii7s5n FOREIGN KEY (receiver_id) REFERENCES public.users (id);

ALTER TABLE ONLY public.mentions
    ADD CONSTRAINT fk8kbxvaojdu8o8r3exxg00cufw FOREIGN KEY (comment_id) REFERENCES public.comments (id);

ALTER TABLE ONLY public.posts
    ADD CONSTRAINT fkh28b7v5xhn84eav8y508akjj0 FOREIGN KEY (subreddit_id) REFERENCES public.subreddits (id);

ALTER TABLE ONLY public.comments
    ADD CONSTRAINT fkh4c7lvsc298whoyd4w9ta25cr FOREIGN KEY (post_id) REFERENCES public.posts (id);

ALTER TABLE ONLY public.votes
    ADD CONSTRAINT fkiavg4g6hf3lpw9lyf1uco7h7v FOREIGN KEY (comment_id) REFERENCES public.comments (id);

ALTER TABLE ONLY public.users_roles
    ADD CONSTRAINT fkj6m8fwv7oqv74fcehir1a9ffy FOREIGN KEY (role_id) REFERENCES public.roles (id);

ALTER TABLE ONLY public.user_subscriptions
    ADD CONSTRAINT fkjunow8dh2ggqwibaw6n1ljh0h FOREIGN KEY (subreddit_id) REFERENCES public.subreddits (id);

ALTER TABLE ONLY public.files
    ADD CONSTRAINT fkkb7cbgxj34ff0yfuf7cqhhh1y FOREIGN KEY (post_id) REFERENCES public.posts (id);

ALTER TABLE ONLY public.votes
    ADD CONSTRAINT fkli4uj3ic2vypf5pialchj925e FOREIGN KEY (user_id) REFERENCES public.users (id);

ALTER TABLE ONLY public.comments
    ADD CONSTRAINT fklri30okf66phtcgbe5pok7cc0 FOREIGN KEY (parent_id) REFERENCES public.comments (id);

ALTER TABLE ONLY public.posts
    ADD CONSTRAINT fkpbdq30fxpf8l0v3j2eyca7odb FOREIGN KEY (creator_id) REFERENCES public.users (id);

ALTER TABLE ONLY public.comments
    ADD CONSTRAINT fkt7f0j94mbyal8bamvf1friujw FOREIGN KEY (creator_id) REFERENCES public.users (id);
