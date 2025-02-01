create database userdb;

grant all privileges on database userdb to hide;

\c userdb
--
-- PostgreSQL database dump
--

-- Dumped from database version 15.3
-- Dumped by pg_dump version 16.3

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

SET default_table_access_method = heap;

--
-- Name: roles; Type: TABLE; Schema: public; Owner: hide
--

CREATE TABLE public.roles (
                              id uuid NOT NULL,
                              role_name character varying(255) NOT NULL,
                              description character varying,
                              created_date timestamp without time zone,
                              created_by character varying(500),
                              updated_date timestamp without time zone,
                              updated_by character varying(500)
);


ALTER TABLE public.roles OWNER TO hide;

--
-- Name: user_role; Type: TABLE; Schema: public; Owner: hide
--

CREATE TABLE public.user_role (
                                  user_id uuid NOT NULL,
                                  role_id uuid NOT NULL
);


ALTER TABLE public.user_role OWNER TO hide;

--
-- Name: users; Type: TABLE; Schema: public; Owner: hide
--

CREATE TABLE public.users (
                              id uuid NOT NULL,
                              email character varying(500) NOT NULL,
                              name character varying(500) NOT NULL,
                              created_date timestamp without time zone,
                              created_by character varying(500),
                              password character varying NOT NULL,
                              updated_date timestamp without time zone,
                              updated_by character varying(500)
);


ALTER TABLE public.users OWNER TO hide;

--
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: hide
--

COPY public.roles (id, role_name, description, created_date, created_by, updated_date, updated_by) FROM stdin;
4cc9f38c-52a5-4b65-9f0a-fa8f8f29bab4	ROLE_ADMIN	\N	2025-02-01 12:29:59	SYSTEM	\N	\N
cdc1f113-8f93-4ffa-8a77-080c720436bc	ROLE_USER	\N	2025-02-01 12:30:20	SYSTEM	\N	\N
7de7c311-c032-46a5-9077-b0356edc852b	ROLE_SA	\N	2025-02-01 12:32:06	SYSTEM	\N	\N
\.


--
-- Data for Name: user_role; Type: TABLE DATA; Schema: public; Owner: hide
--

COPY public.user_role (user_id, role_id) FROM stdin;
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: hide
--

COPY public.users (id, email, name, created_date, created_by, password, updated_date, updated_by) FROM stdin;
\.


--
-- Name: roles roles_pk; Type: CONSTRAINT; Schema: public; Owner: hide
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_pk PRIMARY KEY (id);


--
-- Name: user_role user_role_pk; Type: CONSTRAINT; Schema: public; Owner: hide
--

ALTER TABLE ONLY public.user_role
    ADD CONSTRAINT user_role_pk PRIMARY KEY (role_id, user_id);


--
-- Name: users users_pk; Type: CONSTRAINT; Schema: public; Owner: hide
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pk PRIMARY KEY (id);


--
-- Name: roles_role_name_uindex; Type: INDEX; Schema: public; Owner: hide
--

CREATE UNIQUE INDEX roles_role_name_uindex ON public.roles USING btree (role_name);


--
-- Name: users_email_uindex; Type: INDEX; Schema: public; Owner: hide
--

CREATE UNIQUE INDEX users_email_uindex ON public.users USING btree (email);


--
-- Name: user_role user_role_roles_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: hide
--

ALTER TABLE ONLY public.user_role
    ADD CONSTRAINT user_role_roles_id_fk FOREIGN KEY (role_id) REFERENCES public.roles(id);


--
-- Name: user_role user_role_users_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: hide
--

ALTER TABLE ONLY public.user_role
    ADD CONSTRAINT user_role_users_id_fk FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- PostgreSQL database dump complete
--
