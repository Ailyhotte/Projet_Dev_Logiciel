.gallery-container {
  max-width:fit-content;
  margin: auto;
  padding: 20px;
}

.image-gallery {
  margin-top: 20px;
  display: flex;
  flex-wrap: nowrap; /*Toutes les images sur une seule ligne*/
  overflow-x: auto; /* scrollbar si pas assez de place */
}

.gallery-image {
  width: 100px;
  height: auto;
  margin-right: 10px;
  max-width: 100%;
  max-height: 100%;
}

.image-controls {
  margin-top: 20px;
}

.selected-image {
  margin-top: 20px;
}

.image-and-delete {
  display: flex;
  align-items: center;
}

.image-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.image-wrapper {
  margin-bottom: 10px;
}