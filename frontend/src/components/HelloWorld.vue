<template>
  <div class="gallery-container">
    <h1>{{ GalerieMessage }}</h1>
    <div class="image-gallery">
      <img v-for="image in images" :key="image.id" :src="getImageUrl(image.id)" />
    </div>

    <div class="image-controls">
      <h2>Sélectionner une image à afficher individuellement : </h2>
      <select v-model="selectedImageId" @change="displaySelectedImage">
        <option v-for="image in images" :key="image.id" :value="image.id">
          {{ image.name }} (ID: {{ image.id }})
        </option>
      </select>
    </div>

    <div class="selected-image">
      <img id="selectedImage" />
    </div>
  </div>
  <div class="container">
    <div>
      <label>File
        <input type="file" @change="handleFileUpload($event)" />
      </label>
      <br>
      <button @click="submitFile">Submit</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import axios, { AxiosResponse } from 'axios';

const props = defineProps(['file']);
const data = ref({ file: props.file || '' });

const handleFileUpload = (event: Event) => {
  data.value.file = (event.target as HTMLInputElement).files?.[0] || '';
};



const submitFile = () => {
  let formData = new FormData();

  formData.append('file', data.value.file);
  axios.post('http://localhost:8181/images', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  }).then(() => {
    console.log('Image chargée avec succès');
    fetchImages();
  }).catch(() => {
    console.log("erreur chargement de l'image");
  });
};

const GalerieMessage = "Galerie de toutes les images disponibles";
const images = ref<Array<{ id: number; name: string }>>([]);
const selectedImageId = ref(null);

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
    downloadAndDisplayImage(imageUrl);
  } else {
    console.error("Image sélectionnée non trouvée.");
  }
};

const downloadAndDisplayImage = (imageUrl: string): void => {
  const imgContainer = document.getElementById("selectedImage");
  if (imgContainer != null) {
    axios.get(imageUrl, { responseType: "blob" })
      .then((response: AxiosResponse) => {
        const reader = new window.FileReader();
        reader.readAsDataURL(response.data);
        reader.onload = () => {
          const imageDataUrl = reader.result as string;
          imgContainer.setAttribute("src", imageDataUrl);
        };
      })
      .catch((error: any) => {
        console.log(error);
      });
  } else {
    console.warn("Image non trouvée");
  }
};

onMounted(fetchImages); //appel au lancement de la page au cas ou il y auraient déja des images sur le serveur tomcat
</script>


<style scoped>
@import './MyComponentStyles.vue';
</style>