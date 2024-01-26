<template>
  <div>
    <h1>{{ greetingMessage }}</h1>
    <img/>
    <button id="downloadButton">Télécharger l'image sélectionnée</button>
    <button @click="fetchImages">Recharger les images</button>
    <select v-model="selectedImageId" v-if="images.length > 0">
      <option v-for="image in images" :key="image.id" :value="image.id">
        {{ image.name }} (ID: {{ image.id }})
      </option>
    </select>
    <div v-else>
      Chargement en cours...
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, defineProps, defineEmits } from 'vue'
import axios from 'axios';

const props = defineProps(['msg']);
const emits = defineEmits(['downloadSelectedImage']);
interface Image {
  id: number;
  name: string;
}

const greetingMessage = "Bienvenue dans mon projet Vue !";
const images = ref([] as Image[]);
const selectedImageId = ref<number | null>(null);

const fetchImages = async () => {
  try {
    const response = await axios.get('/images');
    images.value = response.data;
    console.log('Liste des images récupérée avec succès:', images.value);

    // Maintenant que le composant est monté, récupérer l'élément du bouton
    const downloadButton = document.getElementById("downloadButton");
    if (downloadButton) {
      downloadButton.addEventListener("click", function () {
        const selectedImage = images.value.find(image => image.id === selectedImageId.value);
        if (selectedImage) {
          const imageUrl = 'images/' + selectedImage.id;
          console.log(selectedImageId);
          // Appeler la fonction avec l'URL de l'image
          downloadAndDisplayImage(imageUrl);
        } else {
          console.error("Image sélectionnée non trouvée.");
        }
      });
    } else {
      console.error("Le bouton n'a pas été trouvé.");
    }
  } catch (error) {
    console.error('Erreur lors de la récupération des images:', error);
  }
};

function downloadAndDisplayImage(imageUrl: string): void {
  const imageEl = document.querySelector("img");
  console.log('nom du filepath :', imageUrl)

  if (imageEl != null) {
    axios.get(imageUrl, { responseType: "blob" })
      .then(function (response: any) {
        console.log('Image récupérée avec succès :', response);
        const reader = new window.FileReader();
        reader.readAsDataURL(response.data);
        reader.onload = function () {
          const imageDataUrl = (reader.result as string);
          imageEl.setAttribute("src", imageDataUrl);
        }
      })
      .catch(function (error: any) {
        console.log(error);
      });
  } else {
    console.warn("L'élément img n'a pas été trouvé dans le document.");
  }
}

// Appeler fetchImages immédiatement lors de la création du composant
fetchImages();
</script>

<style scoped>
/* Styles spécifiques au composant vont ici */
</style>
