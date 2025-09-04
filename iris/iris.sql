--
-- PostgreSQL database dump
--

\restrict WnkReaz5N2LRlfmA013He1BtZ9dNfuNgpSgUN4aZIETzyaBfbJ60mswDOTtz8Q8

-- Dumped from database version 16.10
-- Dumped by pg_dump version 16.10

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

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON SCHEMA public IS '';


--
-- Name: email_string; Type: DOMAIN; Schema: public; Owner: -
--

CREATE DOMAIN public.email_string AS character varying(255)
	CONSTRAINT email_string_check CHECK (((VALUE IS NULL) OR ((VALUE)::text ~ '^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}$'::text)));


--
-- Name: gender_enum; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.gender_enum AS ENUM (
    'unknown',
    'male',
    'female'
);


--
-- Name: mobile_string; Type: DOMAIN; Schema: public; Owner: -
--

CREATE DOMAIN public.mobile_string AS character varying(255)
	CONSTRAINT mobile_string_check CHECK (((VALUE IS NULL) OR ((VALUE)::text ~ '^1\d{10}$'::text)));


--
-- Name: non_space_string; Type: DOMAIN; Schema: public; Owner: -
--

CREATE DOMAIN public.non_space_string AS text
	CONSTRAINT non_space_string_check CHECK (((VALUE IS NULL) OR (VALUE ~ '^\S*$'::text)));


--
-- Name: trimmed_string; Type: DOMAIN; Schema: public; Owner: -
--

CREATE DOMAIN public.trimmed_string AS text
	CONSTRAINT trimmed_string_check CHECK (((VALUE IS NULL) OR (VALUE = btrim(VALUE))));


--
-- Name: username_string; Type: DOMAIN; Schema: public; Owner: -
--

CREATE DOMAIN public.username_string AS character varying(255)
	CONSTRAINT username_format_check CHECK (((VALUE IS NULL) OR ((VALUE)::text ~ '^[a-z0-9]+$'::text)));


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: t_admin; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_admin (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    true_name public.non_space_string DEFAULT ''::character varying NOT NULL,
    role_ids bigint[] NOT NULL,
    enabled boolean DEFAULT false NOT NULL,
    last_sign_in_at timestamp(0) without time zone,
    last_sign_in_ip inet,
    created_at timestamp(0) without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_ip inet NOT NULL,
    last_action_at timestamp(0) without time zone
);


--
-- Name: t_admin_log; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_admin_log (
    id bigint NOT NULL,
    created_at timestamp(0) without time zone DEFAULT CURRENT_TIMESTAMP,
    user_id bigint NOT NULL,
    ip public.non_space_string NOT NULL,
    operation character varying(255) NOT NULL,
    content json NOT NULL,
    duration integer NOT NULL
);


--
-- Name: t_admin_log_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.t_admin_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: t_admin_log_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.t_admin_log_id_seq OWNED BY public.t_admin_log.id;


--
-- Name: t_admin_menu; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_admin_menu (
    id bigint NOT NULL,
    name public.non_space_string NOT NULL,
    type integer NOT NULL,
    page public.non_space_string DEFAULT ''::character varying,
    icon public.non_space_string DEFAULT ''::character varying NOT NULL,
    parent_id bigint DEFAULT 0 NOT NULL,
    cache boolean DEFAULT false NOT NULL,
    enable boolean DEFAULT true NOT NULL,
    visible boolean DEFAULT true NOT NULL,
    route public.non_space_string DEFAULT ''::character varying NOT NULL,
    order_num integer DEFAULT 100 NOT NULL,
    created_at timestamp(0) without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


--
-- Name: t_admin_menu_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.t_admin_menu_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: t_admin_menu_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.t_admin_menu_id_seq OWNED BY public.t_admin_menu.id;


--
-- Name: t_admin_role; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_admin_role (
    id bigint NOT NULL,
    name public.non_space_string NOT NULL,
    order_num integer DEFAULT 100 NOT NULL,
    auth_ids bigint[] NOT NULL,
    enabled boolean DEFAULT false NOT NULL,
    created_at timestamp(0) without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


--
-- Name: t_admin_role_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.t_admin_role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: t_admin_role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.t_admin_role_id_seq OWNED BY public.t_admin_role.id;


--
-- Name: t_admin_user_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.t_admin_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: t_admin_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.t_admin_user_id_seq OWNED BY public.t_admin.id;


--
-- Name: t_ai_chat; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_ai_chat (
    id bigint NOT NULL,
    thread_id bigint NOT NULL,
    prompt text NOT NULL,
    input_token_count bigint NOT NULL,
    output_token_count bigint NOT NULL,
    total_token_count bigint NOT NULL,
    completion text,
    credits_remaining bigint NOT NULL,
    duration integer NOT NULL,
    created_at timestamp(0) without time zone DEFAULT now() NOT NULL,
    credits_used bigint NOT NULL,
    model_id bigint NOT NULL,
    uuid uuid NOT NULL,
    error text
);


--
-- Name: t_ai_chat_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.t_ai_chat_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: t_ai_chat_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.t_ai_chat_id_seq OWNED BY public.t_ai_chat.id;


--
-- Name: t_ai_model; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_ai_model (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    cny_per_token numeric(12,10),
    intro character varying(255) DEFAULT ''::character varying NOT NULL,
    provider_id bigint NOT NULL,
    order_num integer NOT NULL,
    enabled boolean DEFAULT false NOT NULL,
    created_at timestamp(0) without time zone DEFAULT now() NOT NULL,
    sys_name public.trimmed_string DEFAULT ''::text NOT NULL,
    free boolean DEFAULT false NOT NULL,
    open_router_name public.trimmed_string DEFAULT ''::text NOT NULL
);


--
-- Name: COLUMN t_ai_model.cny_per_token; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_ai_model.cny_per_token IS 'cny per token';


--
-- Name: COLUMN t_ai_model.intro; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_ai_model.intro IS 'Introduction';


--
-- Name: t_ai_provider; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_ai_provider (
    id bigint NOT NULL,
    name public.trimmed_string,
    api_compatible public.trimmed_string NOT NULL,
    url public.non_space_string NOT NULL,
    order_num integer NOT NULL,
    api_url public.non_space_string DEFAULT ''::text NOT NULL,
    api_key public.non_space_string DEFAULT ''::text NOT NULL,
    created_at timestamp(0) without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    icon_svg public.trimmed_string DEFAULT ''::text NOT NULL,
    open_router boolean NOT NULL
);


--
-- Name: COLUMN t_ai_provider.order_num; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_ai_provider.order_num IS 'order num';


--
-- Name: t_ai_provider_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.t_ai_provider_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: t_ai_provider_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.t_ai_provider_id_seq OWNED BY public.t_ai_provider.id;


--
-- Name: t_ai_thread; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_ai_thread (
    id bigint NOT NULL,
    created_at timestamp(0) without time zone DEFAULT now() NOT NULL,
    title public.trimmed_string NOT NULL,
    deleted_at timestamp(0) without time zone DEFAULT NULL::timestamp without time zone,
    user_id bigint NOT NULL,
    uuid uuid NOT NULL
);


--
-- Name: t_ai_thread_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.t_ai_thread_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: t_ai_thread_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.t_ai_thread_id_seq OWNED BY public.t_ai_thread.id;


--
-- Name: t_email_log; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_email_log (
    id bigint NOT NULL,
    created_at timestamp(0) without time zone DEFAULT CURRENT_TIMESTAMP,
    user_id bigint,
    "from" public.email_string NOT NULL,
    "to" public.email_string NOT NULL,
    action public.non_space_string NOT NULL,
    data public.trimmed_string,
    err text,
    duration integer NOT NULL,
    ip public.non_space_string NOT NULL,
    uuid uuid NOT NULL
);


--
-- Name: t_email_log_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.t_email_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: t_email_log_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.t_email_log_id_seq OWNED BY public.t_email_log.id;


--
-- Name: t_model_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.t_model_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: t_model_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.t_model_id_seq OWNED BY public.t_ai_model.id;


--
-- Name: t_payment; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_payment (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    name_cn character varying(255) NOT NULL,
    enable boolean DEFAULT false NOT NULL,
    order_num integer NOT NULL,
    update_time timestamp(0) without time zone NOT NULL,
    config text NOT NULL,
    created_at timestamp(0) without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


--
-- Name: COLUMN t_payment.config; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_payment.config IS '配置信息，AES加密';


--
-- Name: t_payment_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.t_payment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: t_payment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.t_payment_id_seq OWNED BY public.t_payment.id;


--
-- Name: t_system; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_system (
    id bigint NOT NULL,
    entity public.non_space_string NOT NULL,
    attribute public.non_space_string NOT NULL,
    value text NOT NULL,
    "desc" character varying(255) DEFAULT ''::character varying NOT NULL,
    created_at timestamp(0) without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


--
-- Name: t_system_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.t_system_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: t_system_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.t_system_id_seq OWNED BY public.t_system.id;


--
-- Name: t_token_log; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_token_log (
    id bigint NOT NULL,
    created_at timestamp(0) without time zone NOT NULL,
    uid bigint NOT NULL,
    amount bigint NOT NULL,
    balance bigint NOT NULL,
    note character varying(255) NOT NULL
);


--
-- Name: TABLE t_token_log; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.t_token_log IS '用户Token交易日志';


--
-- Name: COLUMN t_token_log.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_token_log.id IS '记录ID';


--
-- Name: COLUMN t_token_log.created_at; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_token_log.created_at IS '记录创建时间';


--
-- Name: COLUMN t_token_log.uid; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_token_log.uid IS '用户ID';


--
-- Name: COLUMN t_token_log.amount; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_token_log.amount IS '调整数量';


--
-- Name: COLUMN t_token_log.note; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_token_log.note IS '交易备注';


--
-- Name: t_user; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_user (
    id bigint NOT NULL,
    name public.username_string DEFAULT NULL::character varying,
    email public.email_string DEFAULT NULL::character varying,
    mobile public.mobile_string DEFAULT NULL::character varying,
    gender public.gender_enum,
    level integer DEFAULT 0 NOT NULL,
    avatar public.non_space_string DEFAULT ''::character varying,
    last_sign_in_at timestamp(0) without time zone,
    last_sign_in_ip inet,
    status bigint DEFAULT 0 NOT NULL,
    password character varying(255) NOT NULL,
    created_at timestamp(0) without time zone DEFAULT now() NOT NULL,
    created_ip inet NOT NULL,
    deleted_at timestamp(0) without time zone
);


--
-- Name: TABLE t_user; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.t_user IS '用户表，存储用户基本信息';


--
-- Name: COLUMN t_user.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_user.id IS '用户ID';


--
-- Name: COLUMN t_user.email; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_user.email IS '电子邮件';


--
-- Name: COLUMN t_user.level; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_user.level IS '级别';


--
-- Name: COLUMN t_user.avatar; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_user.avatar IS '头像';


--
-- Name: COLUMN t_user.last_sign_in_at; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_user.last_sign_in_at IS '最近登陆时间';


--
-- Name: COLUMN t_user.last_sign_in_ip; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_user.last_sign_in_ip IS '最近登陆IP';


--
-- Name: COLUMN t_user.status; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_user.status IS '状态:0:正常 1:暂时关闭 2:永久关闭';


--
-- Name: COLUMN t_user.password; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_user.password IS '密码';


--
-- Name: COLUMN t_user.created_at; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_user.created_at IS '注册时间';


--
-- Name: COLUMN t_user.created_ip; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_user.created_ip IS '注册所用IP';


--
-- Name: t_user_hot; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_user_hot (
    user_id bigint NOT NULL,
    credits bigint DEFAULT 0 NOT NULL
)
WITH (fillfactor='80', autovacuum_vacuum_scale_factor='0.05', autovacuum_vacuum_threshold='50');


--
-- Name: TABLE t_user_hot; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.t_user_hot IS 'user hot data';


--
-- Name: t_user_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.t_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: t_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.t_user_id_seq OWNED BY public.t_user.id;


--
-- Name: t_user_log; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.t_user_log (
    id bigint NOT NULL,
    created_at timestamp(0) without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    user_id bigint NOT NULL,
    ip public.non_space_string NOT NULL,
    operation character varying(255) DEFAULT NULL::character varying,
    content json,
    duration integer NOT NULL
);


--
-- Name: TABLE t_user_log; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.t_user_log IS '用户操作日志表';


--
-- Name: COLUMN t_user_log.id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_user_log.id IS '日志ID';


--
-- Name: COLUMN t_user_log.created_at; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_user_log.created_at IS '日志创建时间';


--
-- Name: COLUMN t_user_log.user_id; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_user_log.user_id IS '用户ID';


--
-- Name: COLUMN t_user_log.operation; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_user_log.operation IS '操作类型';


--
-- Name: COLUMN t_user_log.content; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_user_log.content IS '操作内容';


--
-- Name: COLUMN t_user_log.duration; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.t_user_log.duration IS '操作持续时间（毫秒）';


--
-- Name: t_user_log_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.t_user_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: t_user_log_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.t_user_log_id_seq OWNED BY public.t_user_log.id;


--
-- Name: t_user_token_log_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.t_user_token_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: t_user_token_log_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.t_user_token_log_id_seq OWNED BY public.t_token_log.id;


--
-- Name: t_admin id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_admin ALTER COLUMN id SET DEFAULT nextval('public.t_admin_user_id_seq'::regclass);


--
-- Name: t_admin_log id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_admin_log ALTER COLUMN id SET DEFAULT nextval('public.t_admin_log_id_seq'::regclass);


--
-- Name: t_admin_menu id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_admin_menu ALTER COLUMN id SET DEFAULT nextval('public.t_admin_menu_id_seq'::regclass);


--
-- Name: t_admin_role id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_admin_role ALTER COLUMN id SET DEFAULT nextval('public.t_admin_role_id_seq'::regclass);


--
-- Name: t_ai_chat id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_ai_chat ALTER COLUMN id SET DEFAULT nextval('public.t_ai_chat_id_seq'::regclass);


--
-- Name: t_ai_model id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_ai_model ALTER COLUMN id SET DEFAULT nextval('public.t_model_id_seq'::regclass);


--
-- Name: t_ai_provider id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_ai_provider ALTER COLUMN id SET DEFAULT nextval('public.t_ai_provider_id_seq'::regclass);


--
-- Name: t_ai_thread id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_ai_thread ALTER COLUMN id SET DEFAULT nextval('public.t_ai_thread_id_seq'::regclass);


--
-- Name: t_email_log id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_email_log ALTER COLUMN id SET DEFAULT nextval('public.t_email_log_id_seq'::regclass);


--
-- Name: t_payment id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_payment ALTER COLUMN id SET DEFAULT nextval('public.t_payment_id_seq'::regclass);


--
-- Name: t_system id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_system ALTER COLUMN id SET DEFAULT nextval('public.t_system_id_seq'::regclass);


--
-- Name: t_token_log id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_token_log ALTER COLUMN id SET DEFAULT nextval('public.t_user_token_log_id_seq'::regclass);


--
-- Name: t_user id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_user ALTER COLUMN id SET DEFAULT nextval('public.t_user_id_seq'::regclass);


--
-- Name: t_user_log id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_user_log ALTER COLUMN id SET DEFAULT nextval('public.t_user_log_id_seq'::regclass);


--
-- Data for Name: t_admin; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.t_admin (id, user_id, true_name, role_ids, enabled, last_sign_in_at, last_sign_in_ip, created_at, created_ip, last_action_at) FROM stdin;
10001	1		{1}	t	\N	\N	2025-01-01 00:00:00	::1	\N
\.


--
-- Data for Name: t_admin_log; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.t_admin_log (id, created_at, user_id, ip, operation, content, duration) FROM stdin;
\.


--
-- Data for Name: t_admin_menu; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.t_admin_menu (id, name, type, page, icon, parent_id, cache, enable, visible, route, order_num, created_at) FROM stdin;
4	用户	1		User	0	f	t	t	/user	20	2025-01-01 00:00:00
13	菜单管理	2	system/MenuPage	Menu	10	f	t	t	menu	20	2025-01-01 00:00:00
14	系统配置	2	system/ConfigPage	Setting	10	f	t	t	config	90	2025-01-01 00:00:00
18	系统信息	2	system/InfoPage	Warning	10	t	t	t	info	100	2025-01-01 00:00:00
19	用户管理	2	user/UserPage	User	4	t	t	t	user	1	2025-01-01 00:00:00
20	修改菜单	3			13	f	t	t	edit	3	2025-01-01 00:00:00
22	查询菜单	3			13	f	t	t	query	1	2025-01-01 00:00:00
23	新建菜单	3			13	f	t	t	add	2	2025-01-01 00:00:00
24	删除菜单	3			13	f	t	t	delete	4	2025-01-01 00:00:00
25	查询管理日志	3			15	f	t	t	query	1	2025-01-01 00:00:00
26	查询角色	3			12	f	t	t	query	1	2025-01-01 00:00:00
27	编辑角色	3			12	f	t	t	edit	3	2025-01-01 00:00:00
28	新建角色	3			12	f	t	t	add	2	2025-01-01 00:00:00
29	删除角色	3			12	f	t	t	delete	4	2025-01-01 00:00:00
32	查询用户	3			19	f	t	t	query	1	2025-01-01 00:00:00
33	更改密码	3			19	f	t	f	changePassword	5	2025-01-01 00:00:00
80	查询AI供应商	3			79	f	t	f	query	10	2025-01-01 00:00:00
94	查询聊天记录	3			93	f	t	t	query	10	2025-01-01 00:00:00
95	添加聊天记录	3			93	f	t	t	add	20	2025-01-01 00:00:00
96	修改聊天记录	3			93	f	t	t	edit	30	2025-01-01 00:00:00
97	删除聊天记录	3			93	f	t	t	delete	40	2025-01-01 00:00:00
99	查询加解密	3			98	f	t	t	query	10	2025-01-01 00:00:00
98	加解密	2	devel/SecurityPage	Lock	89	t	t	t	security	100	2025-01-01 00:00:00
101	查询对话主题	3			100	f	t	t	query	10	2025-01-01 00:00:00
102	添加对话主题	3			100	f	t	t	add	20	2025-01-01 00:00:00
103	修改对话主题	3			100	f	t	t	edit	30	2025-01-01 00:00:00
104	删除对话主题	3			100	f	t	t	delete	40	2025-01-01 00:00:00
111	查询	3			110	f	t	f	query	1	2025-01-01 00:00:00
112	编辑	3			110	f	t	f	edit	1	2025-01-01 00:00:00
110	AI配置	2	ai/ConfigPage	Setting	78	f	t	t	config	100	2025-01-01 00:00:00
100	对话主题	2	ai/ThreadPage	Memo	78	t	t	t	thread	1	2025-01-01 00:00:00
34	查询系统信息	3			18	f	t	f	query	1	2025-01-01 00:00:00
35	查询管理员	3			11	f	t	t	query	1	2025-01-01 00:00:00
37	查询用户日志	3			36	f	t	t	query	1	2025-01-01 00:00:00
38	新建管理员	3			11	f	t	t	add	2	2025-01-01 00:00:00
39	编辑管理员	3			11	f	t	t	edit	3	2025-01-01 00:00:00
40	删除管理员	3			11	f	t	t	delete	90	2025-01-01 00:00:00
15	管理日志	2	system/LogPage	Memo	10	t	t	t	log	99	2025-01-01 00:00:00
36	用户日志	2	user/LogPage	Memo	4	t	t	t	log	90	2025-01-01 00:00:00
12	角色管理	2	system/RolePage	Avatar	10	t	t	t	role	10	2025-01-01 00:00:00
70	支付管理	2	system/PaymentPage	Money	10	f	t	t	payment	25	2025-01-01 00:00:00
71	支付管理查询	3			70	f	t	t	query	1	2025-01-01 00:00:00
72	支付管理修改	3			70	f	t	t	edit	2	2025-01-01 00:00:00
73	调整余额	3			19	f	t	f	changeBalance	10	2025-01-01 00:00:00
75	查询余额记录	3			74	f	t	f	query	1	2025-01-01 00:00:00
74	Token日志	2	user/TokenLogPage	Memo	4	f	t	f	token_log	1	2025-01-01 00:00:00
76	新建用户	3			19	f	t	f	add	3	2025-01-01 00:00:00
11	管理员	2	system/AdminPage	User	10	t	t	t	admin	1	2025-01-01 00:00:00
85	查询模型	3			84	f	t	f	query	10	2025-01-01 00:00:00
86	新建模型	3			84	f	t	f	add	20	2025-01-01 00:00:00
92	添加生成	3			90	f	t	f	add	20	2025-01-01 00:00:00
91	查询	3			90	f	t	f	query	10	2025-01-01 00:00:00
87	编辑模型	3			84	f	t	f	edit	30	2025-01-01 00:00:00
88	删除模型	3			84	f	t	f	delete	40	2025-01-01 00:00:00
81	添加AI供应商	3			79	f	t	f	add	20	2025-01-01 00:00:00
82	编辑AI供应商	3			79	f	t	f	edit	30	2025-01-01 00:00:00
83	删除AI供应商	3			79	f	t	f	delete	40	2025-01-01 00:00:00
90	生成代码	2	devel/GenPage	DocumentCopy	89	t	t	t	gen	10	2025-01-01 00:00:00
89	开发工具	1		Monitor	0	f	t	t	/devel	9900	2025-01-01 00:00:00
10	系统	1		SetUp	0	f	t	t	/system	1000	2025-01-01 00:00:00
43	保存系统配置	3			14	f	t	f	save	20	2025-01-01 00:00:00
42	查询系统配置	3			14	f	t	f	query	10	2025-01-01 00:00:00
78	AI	1		Cpu	0	f	t	t	/ai	10	2025-01-01 00:00:00
93	对话记录	2	ai/ChatPage	ChatLineSquare	78	t	t	t	chat	2	2025-01-01 00:00:00
79	供应商	2	ai/ProviderPage	CollectionTag	78	t	t	t	provider	10	2025-01-01 00:00:00
84	模型管理	2	ai/ModelPage	Cellphone	78	t	t	t	model	5	2025-01-01 00:00:00
\.


--
-- Data for Name: t_admin_role; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.t_admin_role (id, name, order_num, auth_ids, enabled, created_at) FROM stdin;
1	超级管理员	100	{}	t	2025-01-01 00:00:00
\.


--
-- Data for Name: t_ai_chat; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.t_ai_chat (id, thread_id, prompt, input_token_count, output_token_count, total_token_count, completion, credits_remaining, duration, created_at, credits_used, model_id, uuid, error) FROM stdin;
\.


--
-- Data for Name: t_ai_model; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.t_ai_model (id, name, cny_per_token, intro, provider_id, order_num, enabled, created_at, sys_name, free, open_router_name) FROM stdin;
\.


--
-- Data for Name: t_ai_provider; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.t_ai_provider (id, name, api_compatible, url, order_num, api_url, api_key, created_at, icon_svg, open_router) FROM stdin;
\.


--
-- Data for Name: t_ai_thread; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.t_ai_thread (id, created_at, title, deleted_at, user_id, uuid) FROM stdin;
\.


--
-- Data for Name: t_email_log; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.t_email_log (id, created_at, user_id, "from", "to", action, data, err, duration, ip, uuid) FROM stdin;
\.


--
-- Data for Name: t_payment; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.t_payment (id, name, name_cn, enable, order_num, update_time, config, created_at) FROM stdin;
\.


--
-- Data for Name: t_system; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.t_system (id, entity, attribute, value, "desc", created_at) FROM stdin;
8	sys	daily_email_limit	6		2025-01-01 00:00:00
9	sys	daily_sms_limit	10		2025-01-01 00:00:00
7	sys	site_name	DeepGet		2025-01-01 00:00:00
6	sys	site_url	https://deepget.net		2025-01-01 00:00:00
5	sys	icp_number	1	备案号	2025-01-01 00:00:00
18	ai	memory_count	10		2025-01-01 00:00:00
20	sys	real_ip_header	CF-Connecting-IP	真实用户IP头	2025-01-01 00:00:00
1	mail	smtp_servers		encrypt data	2025-01-01 00:00:00
15	devel	frontend_path		前端项目路径	2025-01-01 00:00:00
19	ai	open_router_config		encrypt data	2025-01-01 00:00:00
10	sys	rsa_private_key	wnzWqP4Fntza4LFLLZvH+betvQsgPPwM0AqlFeCA3FiRfqqHcPgg0s1ir3AnsCbLMZtVvOdKlsF/Gjn7XRkuHfT5u35l+3raUQ+4WVWO1P2G9FLcvFJ/ABWPiDLj4e7GHZ+Zd8ZoGNrnfYWrUj7KGd9Is+XDSNLmOw6T+Zn/FyIzwZ3/8nngqD2bhTpkjG3NIQWoUgZA5bQ9lWDA9FBxR26iMfUE+B9/qKEXVpXAahtWuRU0yahkplIMAXmniiAUAyxyRKlmlNrWalNn/obOG5ZQPFZV54bA3XlzgmaQdwZvyyldxaJTj6kZ1TNmLSEybnRIr1Y5ubrlU+jcgPqIAH43hnoNe1dz3DQiC3S592/OUf/oHEc95Xfd5Xr/nOmYAR8kRdY6hWHk/vgHlEDVd8yjSWkMClfst4imG7J+8i/YsL9iLfG4Gvv1iaTe/xve+VuDheNPUjWxzGun87WwG+aqoptIB0+d+chilQFQ2Mngrh89YpMlgxhtzske10/izA7j4hSQ69vqWQoadH4o0pQneaZ5PWckCGPAeIFDfFL09lN326Wwcn/vcK0eUbeke1xoXp9V9aQRePKK63G8DjMZ+8S9rE9pGa3fgwyC92dxJIw3mjUMoi6IohFpztnCTqT9Euwm+RUKJ7YmT7wfDRRtbGwzrP5rsDAKvCyRrSPYAnyHobNIaOos8i5R1htpcqIem2tx6Y2nCfRzxcG3tzPUJnlnd39ZyaIvHx2rkV3TwlgqTddnlIOtKgAY1c9ujPscvwWg1tNjLbG3lnM2QNl3bW8uVdT1PbgftcTBzzKBixZllsWa04wN6VxkWPR/ePF4eWqQ7YQpfaRnkibBs9tTpx+CY8px/pC3fM9rDVDbCtuwZi5b7rHoAQ0ZyhYp2+F0sSnpEATy9aV2P4BI0N1CViOVfyRNtrn2VLmLknqOfrO55xXQWCDolKCGDNrCbXIdvDNKJLFClzOZdaH/YYICr+i8fBrfQThwEVtR6RCInz+Q6xj3NB3AOK8y8keVb9Eib95ZQKjMEMDYe9WkzfyY/4aNNmr1tQgfPMAeJGkm9PVMta9kf46lvHFjtqbY7JF69VPW3saQDiqg04axQ7Vu4OpKttmmnUtrgc7b7Wu88IxrrJBuOEcSuEYztv/WuVlkBWVjDEg2u4NzYC0h6ZTM9NntgNkGcsByQeBf1l8SZ1KjSHj4+NiMFsuVXSAO7CVHsJs+KpbVuY6HVD/Q5w2/j/WR/53EmnEiimIivm/hh2k6dNs06FXDSoR5o5OIk/dgT8XRDZ0Br7bcaqRWHv7gV6ZfJPln6nwSY7O6jxJjiGmf9PYbEQnPqa5UHnN8xnRddFyQ7NFmDsXm+Hm3FBC8hyV7hE4y6aEo3GvzQaMu2DXMdxD94A+YP6tMSWn+cUuKUatu8Nz01TbUVQoN0l3IajYpt6cGms+o1pQ9Zhx5bbKFYSVtlOcRdlV1ygQtacjN5T1dTmNHEZ1ZTWbi/DXJHZmJqlNmI/GQ4sp4CSsOpw52cWW8xVX7gq3PBlcnGz4XcQvi6/9d/7uRAlrljbd8ZOFzk+34jkmSnUKvNxm2NAhWoQMSrm583HDJI/BggsAjXyn+Ngdzs4Ps69/UCZMvUZGkyx2e1A+LPuRPAdJDnIOk6YW9ksReT5N+/GJkuiuRhZQdVwZ2OAh719rHlZT9RNVXJXs/LV6A1koKnp6wRs8OE8ghaDt3J5ENiXv5FfFdVlzSks6zEDcngs4OyO8rsY6TGUylbozjJmX2J64PLSCLXHxalgPs3NM+zJ/JbTLBUzXjimrcZxuspPC6CXXnhPPNU0x2WTVGyxl/OqwgWrp31Z3Z5DVtnLjC06Xj/kWePhUc0arP0J8fn7pcLZEFxhoWV7VxSiagFeeq+ebNkDyMEP6Ii2038hMEHQusn0befFGZ08PZpEX3cD3rrxhFZZRXcS5Gml9GJxLAYlIB6sHaCyyxOHRaxsNUdi5nijqwoHwJpZC1lHq7Q2pGLmgTOo/1I03DppmE37zi9fwEvfjoRW239Q2hxIjPdp42sOeIHOTRmir9YcmYR2v6/Uugtf+bDa69oHOxz3Nr38mN+OKDK9jafciLZ71uWCfvIaQoTNbxdrLu6aj59/kcRXhWk6+YNsdj5WLIiSaUieMhC2GKyIMhWRq8JS2qOxl3	encrypt data	2025-01-01 00:00:00
11	sys	rsa_public_key	MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAo45mq2z0v/xAsiyQdoI2ObQTWf3QE9LrW9PkhPa0cEsEnrkPqB6TerioahlfzUzuZLLhDmrX5wiNONGVXqqYDWvKy8mntr8J07hwwWvjgKkTG7jKy47DPGoI3bBxgGWSf71lAz5aK+5S5bX/VAgd94Ho+eUK+PvhN6JIF8APBCzcOJUZwntL39yPzazAElASNcPBJ59FObxOLO0wjOZ1MZnj9IIPMNIA+GoUXviSyGKTzmEwTdy29yGYLH/+Qk4EMAghbfQnngAQIztxiNS0B8QU11YIh+EXbeDKmd4q8clc/mYB1oIFOSOe46d6W6N8gkC33WuRw/ub2A1M+RFO6wIDAQAB	rsa public key	2025-01-01 00:00:00
\.


--
-- Data for Name: t_token_log; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.t_token_log (id, created_at, uid, amount, balance, note) FROM stdin;
\.


--
-- Data for Name: t_user; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.t_user (id, name, email, mobile, gender, level, avatar, last_sign_in_at, last_sign_in_ip, status, password, created_at, created_ip, deleted_at) FROM stdin;
1	demo	\N	\N	\N	0		\N	\N	0	$2a$06$OiCjQh4zb849q1i9de1mMOsRog4ZrJ3HtIRZM8WPGpE1QzBt.yxL.	2025-01-01 00:00:00	::1	\N
\.


--
-- Data for Name: t_user_hot; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.t_user_hot (user_id, credits) FROM stdin;
1	100000
\.


--
-- Data for Name: t_user_log; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.t_user_log (id, created_at, user_id, ip, operation, content, duration) FROM stdin;
\.


--
-- Name: t_admin_log_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.t_admin_log_id_seq', 10001, false);


--
-- Name: t_admin_menu_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.t_admin_menu_id_seq', 10001, false);


--
-- Name: t_admin_role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.t_admin_role_id_seq', 10001, false);


--
-- Name: t_admin_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.t_admin_user_id_seq', 10001, true);


--
-- Name: t_ai_chat_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.t_ai_chat_id_seq', 10001, false);


--
-- Name: t_ai_provider_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.t_ai_provider_id_seq', 10001, false);


--
-- Name: t_ai_thread_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.t_ai_thread_id_seq', 10001, false);


--
-- Name: t_email_log_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.t_email_log_id_seq', 10001, false);


--
-- Name: t_model_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.t_model_id_seq', 10001, false);


--
-- Name: t_payment_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.t_payment_id_seq', 10001, false);


--
-- Name: t_system_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.t_system_id_seq', 10001, false);


--
-- Name: t_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.t_user_id_seq', 10001, false);


--
-- Name: t_user_log_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.t_user_log_id_seq', 10001, false);


--
-- Name: t_user_token_log_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.t_user_token_log_id_seq', 10001, false);


--
-- Name: t_admin_log t_admin_log_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_admin_log
    ADD CONSTRAINT t_admin_log_pkey PRIMARY KEY (id);


--
-- Name: t_admin_menu t_admin_menu_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_admin_menu
    ADD CONSTRAINT t_admin_menu_pkey PRIMARY KEY (id);


--
-- Name: t_admin_role t_admin_role_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_admin_role
    ADD CONSTRAINT t_admin_role_name_key UNIQUE (name);


--
-- Name: t_admin_role t_admin_role_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_admin_role
    ADD CONSTRAINT t_admin_role_pkey PRIMARY KEY (id);


--
-- Name: t_admin t_admin_user_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_admin
    ADD CONSTRAINT t_admin_user_pkey PRIMARY KEY (id);


--
-- Name: t_admin t_admin_user_user_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_admin
    ADD CONSTRAINT t_admin_user_user_id_key UNIQUE (user_id);


--
-- Name: t_ai_chat t_ai_chat_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_ai_chat
    ADD CONSTRAINT t_ai_chat_pk PRIMARY KEY (id);


--
-- Name: t_ai_provider t_ai_provider_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_ai_provider
    ADD CONSTRAINT t_ai_provider_pk PRIMARY KEY (id);


--
-- Name: t_ai_thread t_ai_thread_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_ai_thread
    ADD CONSTRAINT t_ai_thread_pk UNIQUE (uuid);


--
-- Name: t_ai_thread t_ai_thread_pk_2; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_ai_thread
    ADD CONSTRAINT t_ai_thread_pk_2 PRIMARY KEY (id);


--
-- Name: t_email_log t_email_log_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_email_log
    ADD CONSTRAINT t_email_log_pkey PRIMARY KEY (id);


--
-- Name: t_ai_model t_model_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_ai_model
    ADD CONSTRAINT t_model_pkey PRIMARY KEY (id);


--
-- Name: t_payment t_payment_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_payment
    ADD CONSTRAINT t_payment_name_key UNIQUE (name);


--
-- Name: t_payment t_payment_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_payment
    ADD CONSTRAINT t_payment_pkey PRIMARY KEY (id);


--
-- Name: t_system t_system_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_system
    ADD CONSTRAINT t_system_pkey PRIMARY KEY (id);


--
-- Name: t_user t_user_email_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_user
    ADD CONSTRAINT t_user_email_key UNIQUE (email);


--
-- Name: t_user_hot t_user_hot_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_user_hot
    ADD CONSTRAINT t_user_hot_pk PRIMARY KEY (user_id);


--
-- Name: t_user_log t_user_log_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_user_log
    ADD CONSTRAINT t_user_log_pkey PRIMARY KEY (id);


--
-- Name: t_user t_user_mobile_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_user
    ADD CONSTRAINT t_user_mobile_key UNIQUE (mobile);


--
-- Name: t_user t_user_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_user
    ADD CONSTRAINT t_user_name_key UNIQUE (name);


--
-- Name: t_user t_user_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_user
    ADD CONSTRAINT t_user_pkey PRIMARY KEY (id);


--
-- Name: t_token_log t_user_token_log_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_token_log
    ADD CONSTRAINT t_user_token_log_pkey PRIMARY KEY (id);


--
-- Name: t_admin_last_action_at_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX t_admin_last_action_at_index ON public.t_admin USING btree (last_action_at DESC);


--
-- Name: t_ai_chat_thread_id_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX t_ai_chat_thread_id_index ON public.t_ai_chat USING btree (thread_id);


--
-- Name: t_email_log_created_at_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX t_email_log_created_at_index ON public.t_email_log USING btree (created_at DESC);


--
-- Name: t_system_entity_attribute_uindex; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX t_system_entity_attribute_uindex ON public.t_system USING btree (entity, attribute);


--
-- Name: t_token_log_created_at_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX t_token_log_created_at_index ON public.t_token_log USING btree (created_at);


--
-- Name: t_token_log_uid_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX t_token_log_uid_index ON public.t_token_log USING btree (uid);


--
-- Name: t_user_last_sign_in_at_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX t_user_last_sign_in_at_index ON public.t_user USING btree (last_sign_in_at);


--
-- Name: t_ai_model t_ai_model_t_ai_provider_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.t_ai_model
    ADD CONSTRAINT t_ai_model_t_ai_provider_id_fk FOREIGN KEY (provider_id) REFERENCES public.t_ai_provider(id);


--
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: -
--

REVOKE USAGE ON SCHEMA public FROM PUBLIC;


--
-- PostgreSQL database dump complete
--

\unrestrict WnkReaz5N2LRlfmA013He1BtZ9dNfuNgpSgUN4aZIETzyaBfbJ60mswDOTtz8Q8

