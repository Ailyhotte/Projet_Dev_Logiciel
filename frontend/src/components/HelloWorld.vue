<template>
  <div class="gallery-container">
    <h1>{{ GalerieMessage }}</h1>
    <div class="image-gallery">
      <div class="image-and-delete">
        <div v-for="image in images" :key="image.id" class="image-container">
          <div class="image-wrapper">
            <img :src="getImageUrl(image.id)" class="gallery-image" :style="{ maxHeight: maxImageHeightGalery }" />
          </div>
          <button @click="deleteImage(image.id)">Supprimer</button>
        </div>
      </div>
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
      <img id="selectedImage" :style="{ maxHeight: maxSingleImageHeight }" />
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
  const file = (event.target as HTMLInputElement).files?.[0];
  //data.value.file = (event.target as HTMLInputElement).files?.[0] || '';
  if (file) {
    const allowedTypes = ['image/jpeg', 'image/jpg'];
    if (allowedTypes.includes(file.type)) {
      data.value.file = file;
    } else {
      console.error('Seuls les fichiers de type JPEG/JPG sont autorisés.');
      (event.target as HTMLInputElement).value = '';
    }
  };
}

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
const maxImageHeightGalery = ref('25vh');
const maxSingleImageHeight = ref('50vh');

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

const deleteImage = (imageId: number) => {
  if (confirm("Êtes-vous sûr de vouloir supprimer cette image ?")) {
    axios.delete(`/images/${imageId}`)
      .then(() => {
        console.log("Image supprimée avec succès");
        // Mettre à jour la liste des images après la suppression
        fetchImages();
      })
      .catch(error => {
        console.error("Erreur lors de la suppression de l'image :", error);
      });
  }
};

onMounted(fetchImages); // Appel au lancement de la page au cas où il y aurait déjà des images sur le serveur Tomcat
</script>

<style scoped>
@import './MyComponentStyles.vue';
</style>
