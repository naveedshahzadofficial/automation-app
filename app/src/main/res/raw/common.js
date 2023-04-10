(function(){
var verifyBtn = document.querySelector('#VerifyBtn');
var linkBtn = document.querySelector('#link-btn a');
var nextBtn = document.querySelector('#NextBtn');


function callback(mutations, obs) {
  mutations.forEach(function(mutation) {
    if (mutation.type === 'attributes') {
      if (mutation.target.id === 'VerifyBtn' && mutation.target.hidden === false) {
            verifyBtn.click();
            setTimeout(function() {
            if(nextBtn.offsetParent === null){
                         verifyBtn.click();
            }
            }, 5000);
      }
      else if (mutation.target.id ==='NextBtn' && mutation.target.hidden === false) {
            nextBtn.click();
            setTimeout(function() {
                              linkBtn.click();
                          }, 5000);
      }
      else if (linkBtn !== null && mutation.attributeName === 'class' && linkBtn.classList.value==='btn btn-primary rounded get-link xclude-popad' && linkBtn.innerHTML=="Get Link") {
              setTimeout(function() {
                  linkBtn.click();
              }, 3000);
       }
    }
  });
}



if(verifyBtn !== null){

JSBridge.showToast(verifyBtn.innerHTML);

var verifyBtnObserver = new MutationObserver(callback);
verifyBtnObserver.observe(verifyBtn, { attributes: true });
var nextBtnObserver = new MutationObserver(callback);
nextBtnObserver.observe(nextBtn, { attributes: true });

document.getElementById('myTimerDiv').style.display = 'none';
document.getElementById('myNextInst').style.display = 'block';
document.getElementById('VerifyBtn').style.display = 'block';

}

if(linkBtn !== null){
 var linkBtnObserver = new MutationObserver(callback);
 linkBtnObserver.observe(linkBtn, { attributes: true });

     linkBtn.addEventListener("click", function() {
        var JSBridgeVisited = document.querySelector('#JSBridgeVisited');
        if(JSBridgeVisited==undefined){
            var x = document.createElement("input");
            x.setAttribute("id", "JSBridgeVisited");
            x.setAttribute("type", "hidden");
            linkBtn.append(x);
            var get_link = document.querySelector('.get-link');
            get_link.click();
            JSBridge.setCompleted();
            var cookieNames = document.cookie.split(';').map(function(cookie) {
              return cookie.split('=')[0].trim();
            });
            // Loop through the cookie names and expire each one
            cookieNames.forEach(function(cookieName) {
              document.cookie = cookieName + '=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
            });

        }
     });
}

})();