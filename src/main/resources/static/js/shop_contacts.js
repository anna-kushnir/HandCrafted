function openContacts() {
    const list = document.getElementById('contact_list');
    list.style.display = (list.style.display === 'none' ? 'block' : 'none');
}

document.addEventListener('click', function (event) {
   const button = document.getElementById('contact_btn');
   const list = document.getElementById('contact_list');
   if (!button.contains(event.target) && !list.contains(event.target)) {
       list.style.display = 'none';
   }
});