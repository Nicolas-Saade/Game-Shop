import { defineStore } from 'pinia';
import { computed, ref } from 'vue';

export const searchQueryStore = defineStore('search', () => {
    // Define search query as Ref
    const searchQuery = ref('');

    // Update search query Method 
    const setSearchQuery = (query) => {
        searchQuery.value = query;
    }

    // Compute the search query dynamically
    const computedSearchQuery = computed({
        get: () => searchQuery.value,
        set: (value) => {
            searchQuery.value = value;
        },
    });

    // List filterer Just in case
    const listFilterer = (list, query) => {
        return list.filter((item) => {
            return item.name.toLowerCase().includes(query.toLowerCase());
        });
    };

    return {
        searchQuery: computedSearchQuery,
        setSearchQuery,
    };
});