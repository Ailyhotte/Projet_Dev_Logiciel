<script setup lang="ts">
import axios from 'axios';

interface Image {
  id: number;
  name: string;
}

const data = {
  greetingMessage: "Bienvenue dans mon projet Vue !",
  images: [] as Image[],
  selectedImageId: null as number | null
};

const fetchImages = async () => {
  try {
    const response = await axios.get('/images');
    data.images = response.data;
    console.log('Liste des images récupérée avec succès:', data.images);
  } catch (error) {
    console.error('Erreur lors de la récupération des images:', error);
  }
};

// Appeler la fonction fetchImages immédiatement lors de la création du composant
fetchImages();
</script>

<style scoped>
/* Styles spécifiques au composant vont ici */
</style>

<template>
  <div>
    <h1>{{ data.greetingMessage }}</h1>
    <button @click="fetchImages">Recharger les images</button>
    <select v-model="data.selectedImageId">
      <option v-for="image in data.images" :key="image.id" :value="image.id">
        {{ image.name }} (ID: {{ image.id }})
      </option>
    </select>
  </div>
</template>