<template>
  <div>
    <h1>{{ GalerieMessage }}</h1>
    <div>
      <img v-for="image in images" :key="image.id" :src="getImageUrl(image.id)" />
    </div>
    <div>
      <select v-model="selectedImageId" @change="displaySelectedImage">
        <option v-for="image in images" :key="image.id" :value="image.id">
          {{ image.name }} (ID: {{ image.id }})
        </option>
      </select>
      <button @click="displaySelectedImage">Afficher l'image sélectionnée</button>
      <img id="selectedImage"/>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import axios, { AxiosResponse } from 'axios';

interface Image {
  id: number;
  name: string;
}

const GalerieMessage = "Galerie de toutes les images disponibles";
const images = ref([] as Image[]);
const selectedImageId = ref<number | null>(null);

const fetchImages = async () => {
  try {
    const response = await axios.get('/images');
    images.value = response.data;
    console.log('Liste des images récupérée avec succès:', images.value);
  } catch (error) {
    console.error('Erreur lors de la récupération des images:', error);
  }
};

const getImageUrl = (imageId: number): string => `images/${imageId}`;

const displaySelectedImage = () => {
  const selectedImage = images.value.find(image => image.id === selectedImageId.value);
  if (selectedImage) {
    const imageUrl = getImageUrl(selectedImage.id);
    // Appeler la fonction avec l'URL de l'image
    downloadAndDisplayImage(imageUrl);
  } else {
    console.error("Image sélectionnée non trouvée.");
  }
};

const downloadAndDisplayImage = (imageUrl: string): void => {
  const imageEl = document.getElementById("selectedImage");
  if (imageEl != null) {
    axios.get(imageUrl, { responseType: "blob" })
      .then(function (response: AxiosResponse) {
        const reader = new window.FileReader();
        reader.readAsDataURL(response.data);
        reader.onload = function () {
          const imageDataUrl = reader.result as string;
          imageEl.setAttribute("src", imageDataUrl);
        };
      })
      .catch(function (error: any) {
        console.log(error);
      });
  } else {
    console.warn("L'élément img n'a pas été trouvé dans le document.");
  }
};

onMounted(fetchImages);
</script>

<style scoped>
/* Styles spécifiques au composant vont ici */
</style>
