import { createApp } from 'vue'
import './assets/global.css'
import App from './App.vue'
import router from './router'
import stores from './stores'
import i18n from './plugins/i18n'
import Vue3Toastify, { type ToastContainerOptions } from 'vue3-toastify';

//---- md-editor-v3 begion -----
import { config } from 'md-editor-v3';
import 'md-editor-v3/lib/style.css';

import screenfull from 'screenfull';

import katex from 'katex';
import 'katex/dist/katex.min.css';

import Cropper from 'cropperjs';
import 'cropperjs/dist/cropper.css';

import mermaid from 'mermaid';

import highlight from 'highlight.js';
import 'highlight.js/styles/atom-one-dark.css';

// <3.0
// import prettier from 'prettier';
// import parserMarkdown from 'prettier/parser-markdown';
// >=3.0
import * as prettier from 'prettier';
import parserMarkdown from 'prettier/plugins/markdown';
import TargetBlankExtension from '@/utils/markdown/TargetBlankExtension.js';
//---- md-editor-v3 end -----

const app = createApp(App)
app.use(stores)
app.use(router)
app.use(i18n)
app.use(Vue3Toastify, {
    position: 'top-center'
} as ToastContainerOptions)
app.mount('#app')


config({
    editorExtensions: {
        prettier: {
            prettierInstance: prettier,
            parserMarkdownInstance: parserMarkdown,
        },
        highlight: {
            instance: highlight,
        },
        screenfull: {
            instance: screenfull,
        },
        katex: {
            instance: katex,
        },
        cropper: {
            instance: Cropper,
        },
        mermaid: {
            instance: mermaid,
        },
    },
    markdownItConfig(md) {
        md.set({
            // Disable automatic linking
            linkify: false
        });
        md.use(TargetBlankExtension);
    }
});